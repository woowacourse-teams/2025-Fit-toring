import { API_ENDPOINTS } from '../constants/apiEndpoints';

import { apiClient } from './apiClient';

import type { MentoringDetail } from '../types/MentoringDetail';

export const getMineMentoring = () => {
  return apiClient.get<MentoringDetail>({
    endpoint: API_ENDPOINTS.MINE_MENTORING,
    withCredentials: true,
  });
};
