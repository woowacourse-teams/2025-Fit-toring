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

const useInfiniteCommunityPosts = () => {
  return useInfiniteQuery<
    CommunityPostResponse,
    Error,
    InfiniteData<CommunityPostResponse>,
    QueryKey,
    CommunityPostsPageParam
  >({
    queryKey: ['communityPosts'],
    queryFn: ({ pageParam }) => getCommunityPosts(pageParam),
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
