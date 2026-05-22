import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { PostLikeResponse } from '../../../common/types/like';

export const postCommunityPostLike = async (postId: string) => {
  const response = await apiClient.post({
    endpoint: `${API_ENDPOINTS.POSTS}/${postId}/like`,
    withCredentials: true,
  });

  return (await response.json()) as PostLikeResponse;
};

export const deleteCommunityPostLike = async (postId: string) => {
  const response = await apiClient.delete({
    endpoint: `${API_ENDPOINTS.POSTS}/${postId}/like`,
    withCredentials: true,
  });

  return (await response.json()) as PostLikeResponse;
};
