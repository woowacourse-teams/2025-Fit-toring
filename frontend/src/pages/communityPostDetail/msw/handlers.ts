import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import {
  COMMUNITY_POST_DETAIL,
  GUEST_POST_PASSWORD,
  POST_COMMENTS,
} from './data';

const BASE_URL = process.env.API_BASE_URL;
const COMMUNITY_POST_DETAIL_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId`;
const POST_COMMENTS_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId/comments`;
const GUEST_POST_CHECK_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId/guest-check`;
const DELETE_COMMUNITY_POST_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId`;

const getCommunityPostDetail = http.get(
  COMMUNITY_POST_DETAIL_URL,
  async ({ params }) => {
    const { postId } = params;

    return HttpResponse.json({
      ...COMMUNITY_POST_DETAIL,
      id: Number(postId),
    });
  },
);

const getPostComments = http.get(POST_COMMENTS_URL, async () => {
  return HttpResponse.json(POST_COMMENTS);
});

const postGuestPostCheck = http.post(
  GUEST_POST_CHECK_URL,
  async ({ request }) => {
    const requestBody = (await request.json()) as { guestPassword: string };

    if (requestBody.guestPassword !== GUEST_POST_PASSWORD) {
      return HttpResponse.json(
        { message: '비밀번호가 일치하지 않습니다.' },
        { status: 400 },
      );
    }

    return new HttpResponse(null, { status: 200 });
  },
);

const deleteCommunityPost = http.delete(
  DELETE_COMMUNITY_POST_URL,
  async ({ request }) => {
    const requestBody = (await request.json().catch(() => null)) as
      | { guestPassword?: string }
      | null;

    if (
      COMMUNITY_POST_DETAIL.isGuestPost &&
      requestBody?.guestPassword !== GUEST_POST_PASSWORD
    ) {
      return HttpResponse.json(
        { message: '비밀번호가 일치하지 않습니다.' },
        { status: 400 },
      );
    }

    return new HttpResponse(null, { status: 204 });
  },
);

export const communityPostDetailHandler = [
  getCommunityPostDetail,
  getPostComments,
  postGuestPostCheck,
  deleteCommunityPost,
];
