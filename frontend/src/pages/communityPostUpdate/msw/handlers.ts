import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';
import { GUEST_POST_PASSWORD } from '../../communityPostDetail/msw/data';

import type { PatchCommunityPostRequest } from '../apis/patchCommunityPost';

const BASE_URL = process.env.API_BASE_URL;
const PATCH_COMMUNITY_POST_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId`;
const PATCH_GUEST_COMMUNITY_POST_URL = `${BASE_URL}${API_ENDPOINTS.GUEST}${API_ENDPOINTS.POSTS}/:postId`;

const patchCommunityPost = http.patch(
  PATCH_COMMUNITY_POST_URL,
  async () => {
    return new HttpResponse(null, { status: 200 });
  },
);

const patchGuestCommunityPost = http.patch(
  PATCH_GUEST_COMMUNITY_POST_URL,
  async ({ request }) => {
    const requestBody = (await request.json()) as PatchCommunityPostRequest;

    if (
      requestBody.guestPassword !== undefined &&
      requestBody.guestPassword !== GUEST_POST_PASSWORD
    ) {
      return HttpResponse.json(
        { message: '비밀번호가 일치하지 않습니다.' },
        { status: 400 },
      );
    }

    return new HttpResponse(null, { status: 200 });
  },
);

export const communityPostUpdateHandler = [
  patchCommunityPost,
  patchGuestCommunityPost,
];
