import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { MentoringResponse } from '../types/MentoringResponse';

export const getMentoringDetail = async (mentoringId: string) => {
  return await apiClient.get<MentoringResponse>({
    endpoint: `${API_ENDPOINTS.MENTORINGS}/${mentoringId}`,
    withCredentials: true,
  });
};
