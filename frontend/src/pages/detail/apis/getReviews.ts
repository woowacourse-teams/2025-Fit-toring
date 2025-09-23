import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { ReviewResponse } from '../types/ReviewResponse';

export const getReviews = async (mentoringId: number) => {
  return await apiClient.get<ReviewResponse[]>({
    endpoint: `${API_ENDPOINTS.MENTORINGS}/${mentoringId}${API_ENDPOINTS.REVIEWS}`,
    searchParams: { mentoringId: mentoringId.toString() },
    withCredentials: true,
  });
};
