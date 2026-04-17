import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import { CREATED_COMMUNITY_POST } from './data';

import type { CommunityPostDetail } from '../../../common/types/communityPost';
import type { CommunityPostFormValues } from '../../../common/types/communityPostForm';

const BASE_URL = process.env.API_BASE_URL;
const POSTS_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}`;

const postCommunityPostCreate = http.post(POSTS_URL, async ({ request }) => {
  const requestBody = (await request.json()) as CommunityPostFormValues;
  const isGuestPost = Boolean(requestBody.guestPassword);

  const responseBody: CommunityPostDetail = {
    ...CREATED_COMMUNITY_POST,
    title: requestBody.title,
    content: requestBody.content,
    nickname: requestBody.nickname ?? CREATED_COMMUNITY_POST.nickname,
    isAnonymous: requestBody.isAnonymous ?? false,
    isGuestPost,
    isMine: !isGuestPost,
  };

  return HttpResponse.json(responseBody, { status: 201 });
});

export const communityPostCreateHandler = [postCommunityPostCreate];
