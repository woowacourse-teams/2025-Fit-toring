import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export interface CommentOwnershipResponse {
  mineCommentIds: number[];
}

export const getCommunityPostCommentOwnership = async (postId: string) => {
  return await apiClient.get<CommentOwnershipResponse>({
    endpoint: `${API_ENDPOINTS.POSTS}/${postId}/comments/mine`,
    withCredentials: true,
  });
};
