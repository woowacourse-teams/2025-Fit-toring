import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { ParticipatedMentoringType } from '../types/participatedMentoring';

export const getParticipatedMentoringList = async () => {
  return await apiClient.get<ParticipatedMentoringType[]>({
    endpoint: API_ENDPOINTS.PARTICIPATED_MENTORING,
    withCredentials: true,
  });
};
