import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import { CREATED_COMMUNITY_POST } from './data';

import type { CommunityPostDetail } from '../../../common/types/communityPost';
import type { CommunityPostFormValues } from '../../../common/types/communityPostForm';

const BASE_URL = process.env.API_BASE_URL;
const POSTS_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}`;
const GUEST_POSTS_URL = `${BASE_URL}${API_ENDPOINTS.GUEST}${API_ENDPOINTS.POSTS}`;

const createPostResponse = async (request: Request, isGuestPost: boolean) => {
  const requestBody = (await request.json()) as CommunityPostFormValues;

  const responseBody: CommunityPostDetail = {
    ...CREATED_COMMUNITY_POST,
    title: requestBody.title,
    content: requestBody.content,
    nickname: requestBody.nickname ?? CREATED_COMMUNITY_POST.nickname,
    isAnonymous: isGuestPost ? false : (requestBody.isAnonymous ?? false),
    isGuestPost,
  };

  return HttpResponse.json(responseBody, { status: 201 });
};

const postCommunityPostCreate = http.post(POSTS_URL, async ({ request }) =>
  createPostResponse(request, false),
);

const postGuestCommunityPostCreate = http.post(
  GUEST_POSTS_URL,
  async ({ request }) => createPostResponse(request, true),
);

export const communityPostCreateHandler = [
  postCommunityPostCreate,
  postGuestCommunityPostCreate,
];
