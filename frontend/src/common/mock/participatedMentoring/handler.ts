import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

import { PARTICIPATED_MENTORING_LIST } from './data';

export const testStateStore = {
  shouldFailRequest: false,
  customRequestError: null as string | null,
  reset() {
    this.shouldFailRequest = false;
    this.customRequestError = null;
  },
};

const BASE_URL = process.env.API_BASE_URL;
const PARTICIPATED_MENTORING_LIST_URL = `${BASE_URL}${API_ENDPOINTS.PARTICIPATED_MENTORING}`;

const getParticipatedMentoringList = http.get(
  PARTICIPATED_MENTORING_LIST_URL,
  () => {
    const response = PARTICIPATED_MENTORING_LIST;

    if (testStateStore.shouldFailRequest) {
      return HttpResponse.json(
        { message: 'participated mentoring fetch Failed' },
        { status: 401 },
      );
    }

    return HttpResponse.json(response);
  },
);

export const participatedMentoringHandler = [getParticipatedMentoringList];
