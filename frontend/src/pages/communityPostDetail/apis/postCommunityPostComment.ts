import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type {
  PostCommentRequest,
  PostCommentResponse,
} from '../types/postComment';

interface PostCommunityPostCommentParams {
  postId: string;
  commentData: PostCommentRequest;
  isGuestComment: boolean;
}

export const postCommunityPostComment = async ({
  postId,
  commentData,
  isGuestComment,
}: PostCommunityPostCommentParams) => {
  const endpoint = isGuestComment
    ? `${API_ENDPOINTS.GUEST}${API_ENDPOINTS.POSTS}/${postId}/comments`
    : `${API_ENDPOINTS.POSTS}/${postId}/comments`;

  const response = await apiClient.post<PostCommentRequest>({
    endpoint,
    body: commentData,
    withCredentials: true,
  });

  return (await response.json()) as PostCommentResponse;
};
