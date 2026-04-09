import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { CommunityPostResponse } from '../types/posts';

export const getCommunityPosts = async () => {
  return await apiClient.get<CommunityPostResponse>({
    endpoint: `${API_ENDPOINTS.POSTS}`,
    withCredentials: true,
  });
};
