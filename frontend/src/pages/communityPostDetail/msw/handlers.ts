import { http, HttpResponse } from 'msw';

import { COMMUNITY_POST_DETAIL } from './data';

const BASE_URL = process.env.API_BASE_URL;
const COMMUNITY_POST_DETAIL_URL = `${BASE_URL}/posts/:postId`;

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

export const communityPostDetailHandler = [getCommunityPostDetail];
