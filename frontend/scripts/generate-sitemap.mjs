import { mkdir, readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import process from 'node:process';

const SITE_URL = 'https://www.fittoring.com';
const MENTORINGS_PAGE_ENDPOINT = '/mentorings-page';
const GENERATED_SITEMAP_PATH = path.resolve('.generated/sitemap.xml');
const DEFAULT_SORT_KEY = 'CREATED_AT';
const REQUEST_TIMEOUT_MS = 10000;
const MAX_PAGE_COUNT = 200;

const getMode = () => {
  const modeArg = process.argv.find((arg) => arg.startsWith('--mode='));
  const mode = modeArg?.split('=')[1] ?? process.env.APP_ENV ?? 'development';

  return mode === 'production' ? 'production' : 'development';
};

const parseEnvFile = async (envFilePath) => {
  try {
    const content = await readFile(envFilePath, 'utf-8');

    return Object.fromEntries(
      content
        .split('\n')
        .map((line) => line.trim())
        .filter((line) => line && !line.startsWith('#'))
        .map((line) => {
          const separatorIndex = line.indexOf('=');
          const key = line.slice(0, separatorIndex).trim();
          const value = line.slice(separatorIndex + 1).trim();

          return [key, value];
        }),
    );
  } catch {
    return {};
  }
};

const loadEnv = async (mode) => {
  const envFileName = mode === 'production' ? '.env.prod' : '.env.dev';
  const envFile = await parseEnvFile(path.resolve(envFileName));

  return {
    ...envFile,
    ...process.env,
  };
};

const assertMentoringPageResponse = (data) => {
  if (
    !data ||
    !Array.isArray(data.mentoringSummaryResponses) ||
    typeof data.hasNext !== 'boolean'
  ) {
    throw new Error('멘토링 목록 API 응답 형식이 올바르지 않습니다.');
  }
};

const fetchMentoringPage = async ({ apiBaseUrl, cursorCode }) => {
  const url = new URL(MENTORINGS_PAGE_ENDPOINT, apiBaseUrl);
  url.searchParams.set('categoryIds', '');
  url.searchParams.set('sortKey', DEFAULT_SORT_KEY);

  if (cursorCode) {
    url.searchParams.set('cursorCode', cursorCode);
  }

  const response = await fetch(url, {
    headers: {
      accept: 'application/json',
    },
    signal: AbortSignal.timeout(REQUEST_TIMEOUT_MS),
  });

  if (!response.ok) {
    throw new Error(`멘토링 목록 API 요청 실패: ${response.status}`);
  }

  const data = await response.json();
  assertMentoringPageResponse(data);

  return data;
};

const fetchAllMentoringIds = async (apiBaseUrl) => {
  const mentoringIds = new Set();
  const visitedCursors = new Set();
  let cursorCode;
  let pageCount = 0;

  while (true) {
    if (pageCount >= MAX_PAGE_COUNT) {
      throw new Error(`최대 페이지 수 ${MAX_PAGE_COUNT}개를 초과했습니다.`);
    }

    pageCount += 1;

    const page = await fetchMentoringPage({ apiBaseUrl, cursorCode });

    page.mentoringSummaryResponses.forEach(({ id }) => {
      if (typeof id === 'number' || typeof id === 'string') {
        mentoringIds.add(String(id));
      }
    });

    if (!page.hasNext) {
      break;
    }

    if (!page.nextCursorCode) {
      throw new Error('다음 페이지 cursorCode가 없습니다.');
    }

    if (visitedCursors.has(page.nextCursorCode)) {
      throw new Error('반복되는 cursorCode가 감지되었습니다.');
    }

    visitedCursors.add(page.nextCursorCode);
    cursorCode = page.nextCursorCode;
  }

  return [...mentoringIds];
};

const escapeXml = (value) => {
  return value
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&apos;');
};

const buildSitemapXml = (paths) => {
  const urls = paths
    .map((urlPath) => {
      const url = new URL(urlPath, SITE_URL).toString();

      return `  <url>\n    <loc>${escapeXml(url)}</loc>\n  </url>`;
    })
    .join('\n');

  return `<?xml version="1.0" encoding="UTF-8"?>\n<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n${urls}\n</urlset>\n`;
};

const writeSitemap = async (mentoringIds) => {
  const paths = ['/', ...mentoringIds.map((id) => `/detail/${id}`)];
  const sitemapXml = buildSitemapXml(paths);

  await mkdir(path.dirname(GENERATED_SITEMAP_PATH), { recursive: true });
  await writeFile(GENERATED_SITEMAP_PATH, sitemapXml);

  console.log(
    `sitemap.xml 생성 완료: ${paths.length}개 URL (${GENERATED_SITEMAP_PATH})`,
  );
};

const main = async () => {
  const mode = getMode();

  if (mode === 'development') {
    await writeSitemap([]);
    return;
  }

  const env = await loadEnv(mode);
  const apiBaseUrl = env.API_BASE_URL;

  try {
    if (!apiBaseUrl) {
      throw new Error('API_BASE_URL이 설정되지 않았습니다.');
    }

    const mentoringIds = await fetchAllMentoringIds(apiBaseUrl);

    if (mode === 'production' && mentoringIds.length === 0) {
      throw new Error('생성할 멘토링 상세 URL이 없습니다.');
    }

    await writeSitemap(mentoringIds);
  } catch (error) {
    console.error(error);
    process.exit(1);
  }
};

main();
