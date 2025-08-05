import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

import { MENTORING_APPLICATIONS } from './data';

export const testStateStore = {
  shouldFail: false,
  customError: null as string | null,
  reset() {
    this.shouldFail = false;
    this.customError = null;
  },
};

const BASE_URL = process.env.BASE_URL;
const CREATED_MENTORING_URL = `${BASE_URL}${API_ENDPOINTS.CREATED_MENTORING}`;
const getCreatedMentoringList = http.get(CREATED_MENTORING_URL, () => {
  const response = { data: MENTORING_APPLICATIONS };

  if (testStateStore.shouldFail) {
    return new HttpResponse(null, {
      status: 500,
      statusText: 'created mentoring list fetch Failed',
    });
  }

  return HttpResponse.json(response);
});

export const createdMentoringHandler = [getCreatedMentoringList];
