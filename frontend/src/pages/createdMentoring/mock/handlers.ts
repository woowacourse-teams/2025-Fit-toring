import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import { MENTORING_APPLICATIONS } from './data';

import type { MENTORING_APPLICATION_STATUS } from '../types/mentoringApplicationStatus';

export const testStateStore = {
  shouldFail: false,
  customError: null as string | null,
  reset() {
    this.shouldFail = false;
    this.customError = null;
  },
};

const BASE_URL = process.env.API_BASE_URL;
const CREATED_MENTORING_URL = `${BASE_URL}${API_ENDPOINTS.CREATED_MENTORING}`;
const getCreatedMentoringList = http.get(CREATED_MENTORING_URL, () => {
  if (testStateStore.shouldFail) {
    return new HttpResponse(
      { message: 'created mentoring list fetch failed' },
      {
        status: 500,
      },
    );
  }

  return HttpResponse.json(MENTORING_APPLICATIONS);
});

interface PatchReservationStatusBody {
  status: MENTORING_APPLICATION_STATUS;
}

const PATCH_MENTORING_STATUS_URL = `${BASE_URL}${API_ENDPOINTS.RESERVATION}/:reservationId${API_ENDPOINTS.PATCH_MENTORING_STATUS}`;
const patchReservationStatus = http.patch(
  PATCH_MENTORING_STATUS_URL,
  async ({ params, request }) => {
    const { reservationId } = params;
    const body = await request.json();
    const { status } = body as PatchReservationStatusBody;

    if (testStateStore.shouldFail) {
      return new HttpResponse(
        { message: testStateStore.customError || 'Patch failed' },
        {
          status: 400,
        },
      );
    }

    return HttpResponse.json(
      { message: `${reservationId}의 ${status} 업데이트 성공` },
      { status: 200 },
    );
  },
);

export const createdMentoringHandler = [
  getCreatedMentoringList,
  patchReservationStatus,
];
