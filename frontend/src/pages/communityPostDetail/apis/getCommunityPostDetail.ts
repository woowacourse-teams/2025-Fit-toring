import { apiClient } from '../../../common/apis/apiClient';

import type { CommunityPostDetail } from '../../../common/types/communityPost';

export const getCommunityPostDetail = async (postId: string) => {
  return await apiClient.get<CommunityPostDetail>({
    endpoint: `/posts/${postId}`,
    withCredentials: true,
  });
};
