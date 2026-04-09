import { apiClient } from '../../../common/apis/apiClient';

import type { CommunityPostDetail } from '../types/communityPostDetail';

export const getCommunityPostDetail = async (postId: string) => {
  return await apiClient.get<CommunityPostDetail>({
    endpoint: `/posts/${postId}`,
    withCredentials: true,
  });
};
