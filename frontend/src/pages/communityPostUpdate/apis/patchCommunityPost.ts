import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export interface PatchCommunityPostRequest {
  title?: string | null;
  content?: string | null;
  guestPassword?: string;
}

interface PatchCommunityPostParams {
  postId: string;
  postData: PatchCommunityPostRequest;
  isGuestPost: boolean;
}

export const patchCommunityPost = async ({
  postId,
  postData,
  isGuestPost,
}: PatchCommunityPostParams) => {
  const endpoint = isGuestPost
    ? `${API_ENDPOINTS.GUEST}${API_ENDPOINTS.POSTS}/${postId}`
    : `${API_ENDPOINTS.POSTS}/${postId}`;

  await apiClient.patch<PatchCommunityPostRequest>({
    endpoint,
    body: postData,
    withCredentials: true,
  });
};
