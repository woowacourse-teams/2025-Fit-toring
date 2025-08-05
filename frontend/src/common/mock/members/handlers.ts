import { http, HttpResponse } from 'msw';

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
const MEMBER_SUMMARY_URL = `${BASE_URL}/members/summary`;
const getMemberSummary = http.get(MEMBER_SUMMARY_URL, () => {
  const response = { ...MEMBER_SUMMARY };

  if (testStateStore.shouldFailCart) {
    return new HttpResponse(null, {
      status: 500,
      statusText: 'members fetch Failed',
    });
  }

  return HttpResponse.json(response);
});

export const membersHandler = [getMemberSummary];
