import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { CommunityPostDetail } from '../../../common/types/communityPost';
import type { CommunityPostFormValues } from '../../../common/types/communityPostForm';

export const postCommunityPostDetail = async (
  postData: CommunityPostFormValues,
) => {
  const response = await apiClient.post<CommunityPostFormValues>({
    endpoint: API_ENDPOINTS.POSTS,
    body: postData,
    withCredentials: true,
  });

  return (await response.json()) as CommunityPostDetail;
};
