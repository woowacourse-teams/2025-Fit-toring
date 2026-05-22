import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { CommentLikeResponse } from '../../../common/types/like';

interface LikeCommunityPostCommentParams {
  postId: string;
  commentId: number;
}

export const postCommunityPostCommentLike = async ({
  postId,
  commentId,
}: LikeCommunityPostCommentParams) => {
  const response = await apiClient.post({
    endpoint: `${API_ENDPOINTS.POSTS}/${postId}/comments/${commentId}/like`,
    withCredentials: true,
  });

  return (await response.json()) as CommentLikeResponse;
};

export const deleteCommunityPostCommentLike = async ({
  postId,
  commentId,
}: LikeCommunityPostCommentParams) => {
  const response = await apiClient.delete({
    endpoint: `${API_ENDPOINTS.POSTS}/${postId}/comments/${commentId}/like`,
    withCredentials: true,
  });

  return (await response.json()) as CommentLikeResponse;
};
