import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

interface DeleteCommunityPostParams {
  postId: string;
  isGuestPost: boolean;
  guestPassword?: string;
}

export const deleteCommunityPost = async ({
  postId,
  isGuestPost,
  guestPassword,
}: DeleteCommunityPostParams) => {
  const endpoint = isGuestPost
    ? `${API_ENDPOINTS.GUEST}${API_ENDPOINTS.POSTS}/${postId}`
    : `${API_ENDPOINTS.POSTS}/${postId}`;

  await apiClient.delete({
    endpoint,
    body: isGuestPost && guestPassword ? { guestPassword } : undefined,
    withCredentials: true,
  });
};
