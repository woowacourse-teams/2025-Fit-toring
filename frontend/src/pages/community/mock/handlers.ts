import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import { COMMUNITY_POSTS } from './data';

import type { CommunityPost } from '../../../common/types/communityPost';

const BASE_URL = process.env.API_BASE_URL ?? '';
const COMMUNITY_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}`;
const PAGE_SIZE = 10;

const getPageStartIndex = (
  cursorCode: string | null,
  posts: ReadonlyArray<CommunityPost>,
) => {
  if (!cursorCode) {
    return 0;
  }

  const cursorIndex = posts.findIndex(
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
  const keyword = searchParams.get('keyword')?.trim().toLowerCase() ?? '';
  const filteredPosts = keyword
    ? COMMUNITY_POSTS.filter(({ title, content }) =>
        [title, content].some((value) => value.toLowerCase().includes(keyword)),
      )
    : COMMUNITY_POSTS;

  const startIndex = getPageStartIndex(cursorCode, filteredPosts);
  const posts = filteredPosts.slice(startIndex, startIndex + PAGE_SIZE);
  const lastPost = posts[posts.length - 1];
  const hasNext = startIndex + PAGE_SIZE < filteredPosts.length;

  return HttpResponse.json({
    posts,
    nextCursorCode: hasNext && lastPost ? lastPost.id.toString() : null,
    hasNext,
  });
});

export const communityHandler = [getCommunityPosts];
