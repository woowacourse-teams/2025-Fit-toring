import http from 'k6/http';
import exec from 'k6/execution';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import { Rate, Trend } from 'k6/metrics';

const baseUrl = (__ENV.BASE_URL || 'http://localhost:8080').replace(/\/+$/, '');
const scenarioFile = __ENV.SCENARIO_FILE || './reservation-scenarios.json';
const runId = requiredRunId(__ENV.RUN_ID);
const p95BudgetMs = positiveInteger('P95_BUDGET_MS', __ENV.P95_BUDGET_MS, 1500);

const TOTAL_MENTEES = 1000;
const TOTAL_MENTORINGS = 200;
const RESERVATIONS_PER_MENTORING = 5;
const STEADY_RESERVATIONS = 300;
const LOGIN_BATCH_SIZE = 20;

const scenarioRows = new SharedArray('reservation scenarios', () => {
  const rows = JSON.parse(open(scenarioFile));
  validateScenarioRows(rows);
  return rows;
});

const reservationApiDuration = new Trend('reservation_api_duration', true);
const reservationApiFailures = new Rate('reservation_api_failures');

export const options = {
  setupTimeout: '10m',
  scenarios: {
    steady_reservation_create: {
      executor: 'constant-arrival-rate',
      exec: 'createSteadyReservation',
      rate: 1,
      timeUnit: '1s',
      duration: '5m',
      preAllocatedVUs: 5,
      maxVUs: 20,
      gracefulStop: '0s',
    },
    spike_reservation_create: {
      executor: 'constant-arrival-rate',
      exec: 'createSpikeReservation',
      startTime: '5m',
      rate: 5,
      timeUnit: '1s',
      duration: '2m20s',
      preAllocatedVUs: 20,
      maxVUs: 100,
      gracefulStop: '0s',
    },
    outbox_recovery_observation: {
      executor: 'constant-vus',
      exec: 'observeOutboxRecovery',
      startTime: '7m20s',
      vus: 1,
      duration: '22m40s',
      gracefulStop: '0s',
    },
  },
  thresholds: {
    'reservation_api_duration{flow:reservation_create}': [`p(95)<${p95BudgetMs}`],
    'reservation_api_failures{flow:reservation_create}': ['rate<0.01'],
    'checks{flow:reservation_create}': ['rate>0.99'],
    dropped_iterations: ['count==0'],
  },
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

export function setup() {
  const accessTokens = new Array(scenarioRows.length);

  for (let start = 0; start < scenarioRows.length; start += LOGIN_BATCH_SIZE) {
    const batchRows = scenarioRows.slice(start, start + LOGIN_BATCH_SIZE);
    const responses = http.batch(
      batchRows.map((row) => [
        'POST',
        `${baseUrl}/login`,
        JSON.stringify({
          loginId: row.loginId,
          password: row.password,
        }),
        {
          headers: jsonHeaders(),
          tags: {
            name: 'POST /login',
            flow: 'setup_login',
            run_id: runId,
          },
        },
      ]),
    );

    responses.forEach((response, batchIndex) => {
      const rowIndex = start + batchIndex;
      const tokenCookie = response.cookies.accessToken;
      if (response.status !== 200 || !tokenCookie || !tokenCookie[0]) {
        throw new Error(
          `setup login failed: row=${rowIndex}, loginId=${scenarioRows[rowIndex].loginId}, ` +
            `status=${response.status}, body=${response.body}`,
        );
      }
      accessTokens[rowIndex] = tokenCookie[0].value;
    });
  }

  sleep(5);
  return { accessTokens };
}

export function createSteadyReservation(data) {
  executeReservation(data, exec.scenario.iterationInTest);
}

export function createSpikeReservation(data) {
  executeReservation(data, STEADY_RESERVATIONS + exec.scenario.iterationInTest);
}

export function observeOutboxRecovery() {
  sleep(1);
}

function executeReservation(data, rowIndex) {
  const row = scenarioRows[rowIndex];
  const accessToken = data.accessTokens[rowIndex];

  if (!row || !accessToken) {
    exec.test.abort(
      `No scenario data for iteration: scenario=${exec.scenario.name}, row=${rowIndex}`,
    );
  }

  const response = createReservation(row, accessToken, rowIndex);
  const succeeded = check(
    response,
    {
      '예약 생성 응답이 201이다': (res) => res.status === 201,
    },
    { flow: 'reservation_create' },
  );

  reservationApiDuration.add(response.timings.duration, {
    flow: 'reservation_create',
  });
  reservationApiFailures.add(!succeeded, {
    flow: 'reservation_create',
  });

  if (!succeeded) {
    console.error(
      `reservation failed: runId=${runId}, row=${rowIndex}, loginId=${row.loginId}, ` +
        `mentoringId=${row.mentoringId}, status=${response.status}, body=${response.body}`,
    );
  }
}

function createReservation(row, accessToken, rowIndex) {
  return http.post(
    `${baseUrl}/mentorings/${row.mentoringId}/reservation`,
    JSON.stringify({
      content: `${runId}-${String(rowIndex + 1).padStart(4, '0')}`,
    }),
    {
      headers: jsonHeaders(),
      cookies: {
        accessToken: {
          value: accessToken,
          replace: true,
        },
      },
      tags: {
        name: 'POST /mentorings/{mentoringId}/reservation',
        flow: 'reservation_create',
        run_id: runId,
      },
    },
  );
}

function jsonHeaders() {
  return {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  };
}

function validateScenarioRows(rows) {
  if (!Array.isArray(rows) || rows.length !== TOTAL_MENTEES) {
    throw new Error(
      `${scenarioFile} must contain exactly ${TOTAL_MENTEES} rows. rows=${rows.length}`,
    );
  }

  const uniqueLoginIds = new Set();
  const uniquePairs = new Set();
  const mentoringCounts = new Map();

  rows.forEach((row, index) => {
    if (!row || typeof row !== 'object') {
      throw new Error(`scenario row ${index} must be an object.`);
    }
    if (!row.loginId || !row.password || !Number.isInteger(row.mentoringId)) {
      throw new Error(
        `scenario row ${index} requires loginId, password, and integer mentoringId.`,
      );
    }

    const pair = `${row.loginId}:${row.mentoringId}`;
    if (uniquePairs.has(pair)) {
      throw new Error(`duplicated active reservation pair: ${pair}`);
    }
    uniquePairs.add(pair);

    if (uniqueLoginIds.has(row.loginId)) {
      throw new Error(`a mentee can reserve only once: loginId=${row.loginId}`);
    }
    uniqueLoginIds.add(row.loginId);

    mentoringCounts.set(
      row.mentoringId,
      (mentoringCounts.get(row.mentoringId) || 0) + 1,
    );
  });

  if (mentoringCounts.size !== TOTAL_MENTORINGS) {
    throw new Error(
      `scenario requires exactly ${TOTAL_MENTORINGS} mentorings. mentorings=${mentoringCounts.size}`,
    );
  }

  mentoringCounts.forEach((count, mentoringId) => {
    if (count !== RESERVATIONS_PER_MENTORING) {
      throw new Error(
        `mentoringId=${mentoringId} requires ${RESERVATIONS_PER_MENTORING} reservations. count=${count}`,
      );
    }
  });
}

function positiveInteger(name, rawValue, defaultValue) {
  const value = rawValue === undefined || rawValue === '' ? defaultValue : Number(rawValue);
  if (!Number.isInteger(value) || value <= 0) {
    throw new Error(`${name} must be a positive integer. value=${rawValue}`);
  }
  return value;
}

function requiredRunId(rawValue) {
  if (!rawValue || !/^[A-Za-z0-9_-]{1,50}$/.test(rawValue)) {
    throw new Error(
      'RUN_ID is required and must contain 1-50 letters, numbers, hyphens, or underscores.',
    );
  }
  return rawValue;
}
