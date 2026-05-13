import { apiClient } from '../../../common/apis/apiClient';

import type { PostComment } from '../types/postComment';

export const getPostComments = async (postId: string) => {
  return await apiClient.get<PostComment[]>({
    endpoint: `/posts/${postId}/comments`,
    withCredentials: true,
  });
};
