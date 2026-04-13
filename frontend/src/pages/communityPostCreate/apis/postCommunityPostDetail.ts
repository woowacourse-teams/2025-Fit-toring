import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { CommunityPostDetail } from '../../../common/types/communityPost';

export interface PostCommunityPostDetailRequest {
  title: string;
  content: string;
  isAnonymous?: boolean;
  nickname?: string;
  guestPassword?: string;
}

export const postCommunityPostDetail = async (
  postData: PostCommunityPostDetailRequest,
) => {
  const response = await apiClient.post<PostCommunityPostDetailRequest>({
    endpoint: API_ENDPOINTS.POSTS,
    body: postData,
    withCredentials: true,
  });

  return (await response.json()) as CommunityPostDetail;
};
