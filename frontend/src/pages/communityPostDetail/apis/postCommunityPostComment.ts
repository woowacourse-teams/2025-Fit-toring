import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { PostComment, PostCommentRequest } from '../types/postComment';

export const postCommunityPostComment = async (
  postId: string,
  commentData: PostCommentRequest,
) => {
  const response = await apiClient.post<PostCommentRequest>({
    endpoint: `${API_ENDPOINTS.POSTS}/${postId}/comments`,
    body: commentData,
    withCredentials: true,
  });

  return (await response.json()) as PostComment;
};
