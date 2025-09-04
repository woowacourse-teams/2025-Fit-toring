import { API_ENDPOINTS } from '../constants/apiEndpoints';

import { apiClient } from './apiClient';

import type { MentoringResponse } from '../types/MentoringResponse';

export const getMineMentoring = () => {
  return apiClient.get<MentoringResponse>({
    endpoint: API_ENDPOINTS.MINE_MENTORING,
    withCredentials: true,
  });
};
