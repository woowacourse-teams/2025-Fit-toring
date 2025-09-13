import { API_ENDPOINTS } from '../constants/apiEndpoints';

import { apiClient } from './apiClient';

import type { MentoringDetail } from '../types/MentoringDetail';

export const getMentoringDetail = async (mentoringId: string) => {
  return await apiClient.get<MentoringDetail>({
    endpoint: `${API_ENDPOINTS.MENTORINGS}/${mentoringId}`,
    withCredentials: true,
  });
};
