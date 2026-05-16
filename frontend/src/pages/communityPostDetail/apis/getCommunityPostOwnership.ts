import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export interface PostOwnershipResponse {
  isMine: boolean;
}

export const getCommunityPostOwnership = async (postId: string) => {
  return await apiClient.get<PostOwnershipResponse>({
    endpoint: `${API_ENDPOINTS.POSTS}/${postId}/mine`,
    withCredentials: true,
  });
};
