import { apiClient } from '../../../common/apis/apiClient';

import type { PostCommentResponse } from '../types/postComment';

export const getPostComments = async (postId: string) => {
  return await apiClient.get<PostCommentResponse[]>({
    endpoint: `/posts/${postId}/comments`,
    withCredentials: true,
  });
};
