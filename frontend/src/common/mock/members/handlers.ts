import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

import { MEMBER_SUMMARY } from './data';

export const testStateStore = {
  shouldFailCart: false,
  customCartError: null as string | null,
  reset() {
    this.shouldFailCart = false;
    this.customCartError = null;
  },
};

const BASE_URL = process.env.BASE_URL;
const MEMBER_SUMMARY_URL = `${BASE_URL}${API_ENDPOINTS.MEMBERS}`;
const getMemberSummary = http.get(MEMBER_SUMMARY_URL, () => {
  const response = { ...MEMBER_SUMMARY };

  if (testStateStore.shouldFailCart) {
    return HttpResponse.json(
      { message: 'members fetch Failed' },
      { status: 500 },
    );
  }
  return HttpResponse.json(response);
});

export const membersHandler = [getMemberSummary];
