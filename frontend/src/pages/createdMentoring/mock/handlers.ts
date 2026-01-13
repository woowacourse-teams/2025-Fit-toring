import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import { MENTORING_APPLICATIONS } from './data';

export const testStateStore = {
  shouldFail: false,
  customError: null as string | null,
  reset() {
    this.shouldFail = false;
    this.customError = null;
  },
};

const BASE_URL = process.env.API_BASE_URL;
const CREATED_MENTORING_URL = `${BASE_URL}${API_ENDPOINTS.CREATED_MENTORING}`;
const getCreatedMentoringList = http.get(CREATED_MENTORING_URL, () => {
  if (testStateStore.shouldFail) {
    return new HttpResponse(
      { message: 'created mentoring list fetch failed' },
      {
        status: 500,
      },
    );
  }

  return HttpResponse.json(MENTORING_APPLICATIONS);
});

const PATCH_MENTORING_APPROVE_URL = `${BASE_URL}${API_ENDPOINTS.RESERVATIONS}/:reservationId${API_ENDPOINTS.PATCH_MENTORING_APPROVE}`;
const patchReservationApprove = http.patch(
  PATCH_MENTORING_APPROVE_URL,
  async ({ params }) => {
    const { reservationId } = params;

    if (testStateStore.shouldFail) {
      return new HttpResponse(
        { message: testStateStore.customError || 'Approve failed' },
        {
          status: 400,
        },
      );
    }

    return HttpResponse.json(
      { message: `${reservationId} 승인 성공` },
      { status: 200 },
    );
  },
);

const PATCH_MENTORING_REJECT_URL = `${BASE_URL}${API_ENDPOINTS.RESERVATIONS}/:reservationId${API_ENDPOINTS.PATCH_MENTORING_REJECT}`;
const patchReservationReject = http.patch(
  PATCH_MENTORING_REJECT_URL,
  async ({ params }) => {
    const { reservationId } = params;

    if (testStateStore.shouldFail) {
      return new HttpResponse(
        { message: testStateStore.customError || 'Reject failed' },
        {
          status: 400,
        },
      );
    }

    return HttpResponse.json(
      { message: `${reservationId} 거절 성공` },
      { status: 200 },
    );
  },
);

const PATCH_MENTORING_COMPLETE_URL = `${BASE_URL}${API_ENDPOINTS.RESERVATIONS}/:reservationId${API_ENDPOINTS.PATCH_MENTORING_COMPLETE}`;
const patchReservationComplete = http.patch(
  PATCH_MENTORING_COMPLETE_URL,
  async ({ params }) => {
    const { reservationId } = params;

    if (testStateStore.shouldFail) {
      return new HttpResponse(
        { message: testStateStore.customError || 'Complete failed' },
        {
          status: 400,
        },
      );
    }

    return HttpResponse.json(
      { message: `${reservationId} 완료 성공` },
      { status: 200 },
    );
  },
);

export const createdMentoringHandler = [
  getCreatedMentoringList,
  patchReservationApprove,
  patchReservationReject,
  patchReservationComplete,
];
