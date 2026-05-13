import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export interface PatchCommunityPostRequest {
  title?: string | null;
  content?: string | null;
  guestPassword?: string;
}

export const patchCommunityPost = async (
  postId: string,
  postData: PatchCommunityPostRequest,
) => {
  await apiClient.patch<PatchCommunityPostRequest>({
    endpoint: `${API_ENDPOINTS.POSTS}/${postId}`,
    body: postData,
    withCredentials: true,
  });
};
