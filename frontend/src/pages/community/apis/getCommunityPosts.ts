import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { CommunityPostResponse } from '../types/posts';

interface GetCommunityPostsParams {
  cursorCode?: string | null;
}

export const getCommunityPosts = async ({
  cursorCode,
}: GetCommunityPostsParams = {}) => {
  return await apiClient.get<CommunityPostResponse>({
    endpoint: `${API_ENDPOINTS.POSTS}`,
    withCredentials: true,
    ...(cursorCode ? { searchParams: { cursorCode } } : {}),
  });
};
