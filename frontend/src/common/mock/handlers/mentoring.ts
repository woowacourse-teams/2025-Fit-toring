import { http, HttpResponse } from 'msw';

import { MENTORINGS } from '../data/mentorings';

export const testStateStore = {
  shouldFailCart: false,
  customCartError: null as string | null,
  reset() {
    this.shouldFailCart = false;
    this.customCartError = null;
  },
};

const BASE_URL = process.env.BASE_URL;
const MENTORING_URL = `${BASE_URL}/mentorings`;
const getMentoringItems = http.get(MENTORING_URL, () => {
  const response = [...MENTORINGS];

  if (testStateStore.shouldFailCart) {
    return new HttpResponse(null, {
      status: 500,
      statusText: 'mentorings fetch Failed',
    });
  }

  return HttpResponse.json(response);
});

export const mentoringHandler = [getMentoringItems];
