import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import { COMMUNITY_POSTS } from './data';

const BASE_URL = process.env.API_BASE_URL;
const COMMUNITY_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}`;
const PAGE_SIZE = 10;

const getPageStartIndex = (cursorCode: string | null) => {
  if (!cursorCode) {
    return 0;
  }

  const cursorIndex = COMMUNITY_POSTS.findIndex(
    ({ id }) => id.toString() === cursorCode,
  );

  if (cursorIndex === -1) {
    return 0;
  }

  return cursorIndex + 1;
};

const getCommunityPosts = http.get(COMMUNITY_URL, ({ request }) => {
  const { searchParams } = new URL(request.url);
  const cursorCode = searchParams.get('cursorCode');

  const startIndex = getPageStartIndex(cursorCode);
  const posts = COMMUNITY_POSTS.slice(startIndex, startIndex + PAGE_SIZE);
  const lastPost = posts[posts.length - 1];
  const hasNext = startIndex + PAGE_SIZE < COMMUNITY_POSTS.length;

  return HttpResponse.json({
    posts,
    nextCursorCode: hasNext && lastPost ? lastPost.id.toString() : '',
    hasNext,
  });
});

export const communityHandler = [getCommunityPosts];
