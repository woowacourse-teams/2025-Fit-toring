import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { CommunityPostResponse } from '../types/posts';

interface GetCommunityPostsParams {
  cursorCode?: string | null;
  keyword?: string;
}

export const getCommunityPosts = async ({
  cursorCode,
  keyword,
}: GetCommunityPostsParams = {}) => {
  const searchParams = {
    ...(cursorCode ? { cursorCode } : {}),
    ...(keyword ? { keyword } : {}),
  };

  return await apiClient.get<CommunityPostResponse>({
    endpoint: `${API_ENDPOINTS.POSTS}`,
    withCredentials: true,
    ...(Object.keys(searchParams).length > 0 ? { searchParams } : {}),
  });
};
