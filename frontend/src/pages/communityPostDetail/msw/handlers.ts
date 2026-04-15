import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import { COMMUNITY_POST_DETAIL, POST_COMMENTS } from './data';

const BASE_URL = process.env.API_BASE_URL;
const COMMUNITY_POST_DETAIL_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId`;
const POST_COMMENTS_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId/comments`;

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

export const communityPostDetailHandler = [
  getCommunityPostDetail,
  getPostComments,
];
