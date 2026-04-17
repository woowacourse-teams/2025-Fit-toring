import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export interface DeleteCommunityPostCommentRequest {
  guestPassword?: string;
}

export const deleteCommunityPostComment = async (
  commentId: number,
  guestPassword?: string,
) => {
  await apiClient.delete({
    endpoint: `${API_ENDPOINTS.COMMENTS}/${commentId}`,
    ...(guestPassword ? { body: { guestPassword } } : {}),
    withCredentials: true,
  });
};
