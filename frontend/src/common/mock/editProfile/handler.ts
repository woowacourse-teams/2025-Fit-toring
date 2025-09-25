import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

import { USER_PROFILE } from './data';

import type { PartialUserProfileRequest } from '../../../pages/editProfile/types/userProfile';

const BASE_URL = process.env.API_BASE_URL;
const EDIT_PROFILE_URL = `${BASE_URL}${API_ENDPOINTS.MEMBERS_ME}`;

export const testStateStore = {
  shouldFail: false,
  customError: null as string | null,
  reset() {
    this.shouldFail = false;
    this.customError = '내 정보 수정 실패';
  },
};

const patchMyProfile = http.patch(EDIT_PROFILE_URL, async ({ request }) => {
  const body = await request.json();
  const profileData = body as PartialUserProfileRequest;

  if (testStateStore.shouldFail) {
    return new HttpResponse(
      { message: testStateStore.customError || 'Patch failed' },
      {
        status: 400,
      },
    );
  }

  return HttpResponse.json(
    { message: `내 정보 수정 성공`, data: profileData },
    { status: 204 },
  );
});

const USER_INFO_URL = `${BASE_URL}${API_ENDPOINTS.MEMBERS_ME}`;
const getUserInfo = http.get(`${USER_INFO_URL}`, () => {
  const response = { ...USER_PROFILE };

  if (testStateStore.shouldFail) {
    return new HttpResponse({ message: '내 정보 조회 실패' }, { status: 500 });
  }

  return HttpResponse.json(response, { status: 200 });
});

export const editProfileHandlers = [patchMyProfile, getUserInfo];
