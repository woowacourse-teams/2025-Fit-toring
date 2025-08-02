import { http, HttpResponse } from 'msw';

import { SPECIALTIES } from './data';

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

export const specialtiesHandler = [getSpecialties];
