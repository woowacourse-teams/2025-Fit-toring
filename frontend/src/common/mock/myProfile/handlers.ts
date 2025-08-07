import { http, HttpResponse } from 'msw';

import { PROFILE } from './data';

export const testStateStore = {
  shouldFail: false,
  customError: null as string | null,
  reset() {
    this.shouldFail = false;
    this.customError = null;
  },
};

const BASE_URL = process.env.API_BASE_URL;
const MY_PROFILE_URL = `${BASE_URL}/my-page`;
const getMyProfile = http.get(MY_PROFILE_URL, () => {
  const response = { data: PROFILE };

  if (testStateStore.shouldFail) {
    return new HttpResponse(null, {
      status: 500,
      statusText: 'my_profile fetch Failed',
    });
  }

  return HttpResponse.json(response);
});

export const myProfileHandler = [getMyProfile];
