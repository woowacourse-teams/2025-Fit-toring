import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export interface PatchCommunityPostCommentRequest {
  content: string;
  guestPassword?: string;
}

interface PatchCommunityPostCommentParams {
  commentId: number;
  commentData: PatchCommunityPostCommentRequest;
  isGuestComment: boolean;
}

export const patchCommunityPostComment = async ({
  commentId,
  commentData,
  isGuestComment,
}: PatchCommunityPostCommentParams) => {
  const endpoint = isGuestComment
    ? `${API_ENDPOINTS.GUEST}${API_ENDPOINTS.COMMENTS}/${commentId}`
    : `${API_ENDPOINTS.COMMENTS}/${commentId}`;

  await apiClient.patch<PatchCommunityPostCommentRequest>({
    endpoint,
    body: commentData,
    withCredentials: true,
  });
};
