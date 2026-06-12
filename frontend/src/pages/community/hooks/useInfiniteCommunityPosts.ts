import {
  useInfiniteQuery,
  type InfiniteData,
  type QueryKey,
} from '@tanstack/react-query';

import { getCommunityPosts } from '../apis/getCommunityPosts';

import type { CommunityPostResponse } from '../types/posts';

interface CommunityPostsPageParam {
  cursorCode?: string | null;
}

const useInfiniteCommunityPosts = (keyword = '') => {
  const normalizedKeyword = keyword.trim();

  return useInfiniteQuery<
    CommunityPostResponse,
    Error,
    InfiniteData<CommunityPostResponse>,
    QueryKey,
    CommunityPostsPageParam
  >({
    queryKey: ['communityPosts', normalizedKeyword],
    queryFn: ({ pageParam }) =>
      getCommunityPosts({
        ...pageParam,
        ...(normalizedKeyword ? { keyword: normalizedKeyword } : {}),
      }),
    initialPageParam: {},
    getNextPageParam: (lastPage) => {
      if (!lastPage.hasNext) {
        return undefined;
      }

      return {
        cursorCode: lastPage.nextCursorCode,
      };
    },
  });
};

export default useInfiniteCommunityPosts;
