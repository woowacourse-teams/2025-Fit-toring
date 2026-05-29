import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

interface PostCommunityPostCommentGuestCheckParams {
  commentId: number;
  guestPassword: string;
}

export const postCommunityPostCommentGuestCheck = async ({
  commentId,
  guestPassword,
}: PostCommunityPostCommentGuestCheckParams) => {
  await apiClient.post({
    endpoint: `${API_ENDPOINTS.COMMENTS}/${commentId}/pw-check`,
    body: { guestPassword },
    withCredentials: true,
  });

  return true;
};
