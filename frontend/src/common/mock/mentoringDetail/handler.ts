import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

import { MENTORING_DETAIL } from './data';

export const testStateStore = {
  shouldFailRequest: false,
  customRequestError: null as string | null,
  reset() {
    this.shouldFailRequest = false;
    this.customRequestError = null;
  },
};

const BASE_URL = process.env.API_BASE_URL;
const MENTORING_DETAIL_URL = `${BASE_URL}${API_ENDPOINTS.MENTORINGS}/:id`;

const getMentoringDetail = http.get(MENTORING_DETAIL_URL, () => {
  const response = MENTORING_DETAIL;

  if (testStateStore.shouldFailRequest) {
    return HttpResponse.json(
      { message: '멘토링 상세 정보 조회 실페' },
      { status: 400 },
    );
  }

  return HttpResponse.json(response, { status: 200 });
});

export const mentoringDetailHandler = [getMentoringDetail];
