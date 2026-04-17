import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export interface PatchCommunityPostCommentRequest {
  content: string;
  guestPassword?: string;
}

export const patchCommunityPostComment = async (
  commentId: number,
  commentData: PatchCommunityPostCommentRequest,
) => {
  await apiClient.patch<PatchCommunityPostCommentRequest>({
    endpoint: `${API_ENDPOINTS.COMMENTS}/${commentId}`,
    body: commentData,
    withCredentials: true,
  });
};
