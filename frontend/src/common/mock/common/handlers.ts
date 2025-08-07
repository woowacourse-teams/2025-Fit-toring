import { http, HttpResponse } from 'msw';

import { SPECIALTIES, USER_INFO } from './data';
import { API_ENDPOINTS } from '../../constants/apiEndpoints';

export const testStateStore = {
  shouldFail: false,
  customError: null as string | null,
  reset() {
    this.shouldFail = false;
    this.customError = null;
  },
};

const BASE_URL = process.env.BASE_URL;
const SPECIALTIES_URL = `${BASE_URL}/categories`;

const getSpecialties = http.get(SPECIALTIES_URL, () => {
  const response = [...SPECIALTIES];

  if (testStateStore.shouldFail) {
    return new HttpResponse(null, {
      status: 500,
      statusText: 'specialties fetch Failed',
    });
  }

  return HttpResponse.json(response);
});

const USER_INFO_URL = `${BASE_URL}${API_ENDPOINTS.MEMBERS_ME}`;

const getUserInfo = http.get(USER_INFO_URL, () => {
  const response = { ...USER_INFO };

  if (testStateStore.shouldFail) {
    return new HttpResponse(null, {
      status: 500,
      statusText: 'userInfo fetch Failed',
    });
  }

  return HttpResponse.json(response);
});

export const commonHandler = [getSpecialties, getUserInfo];
