import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export interface DeleteCommunityPostCommentRequest {
  guestPassword?: string;
}

interface DeleteCommunityPostCommentParams {
  commentId: number;
  isGuestComment: boolean;
  guestPassword?: string;
}

export const deleteCommunityPostComment = async ({
  commentId,
  isGuestComment,
  guestPassword,
}: DeleteCommunityPostCommentParams) => {
  const endpoint = isGuestComment
    ? `${API_ENDPOINTS.GUEST}${API_ENDPOINTS.COMMENTS}/${commentId}`
    : `${API_ENDPOINTS.COMMENTS}/${commentId}`;

  await apiClient.delete({
    endpoint,
    ...(isGuestComment && guestPassword ? { body: { guestPassword } } : {}),
    withCredentials: true,
  });
};
