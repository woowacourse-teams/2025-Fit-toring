import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { CommunityPostDetail } from '../../../common/types/communityPost';
import type { CommunityPostFormValues } from '../../../common/types/communityPostForm';

interface PostCommunityPostDetailParams {
  postData: CommunityPostFormValues;
  isGuestPost: boolean;
}

export const postCommunityPostDetail = async ({
  postData,
  isGuestPost,
}: PostCommunityPostDetailParams) => {
  const endpoint = isGuestPost
    ? `${API_ENDPOINTS.GUEST}${API_ENDPOINTS.POSTS}`
    : API_ENDPOINTS.POSTS;

  const response = await apiClient.post<CommunityPostFormValues>({
    endpoint,
    body: postData,
    withCredentials: true,
  });

  return (await response.json()) as CommunityPostDetail;
};
