import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { MentoringApplicationResponse } from '../types/mentoringApplication';

export const getMentoringApplicationList = async () => {
  const { data } = await apiClient.get<MentoringApplicationResponse>({
    endpoint: API_ENDPOINTS.CREATED_MENTORING,
    withCredentials: true,
  });

  return data;
};
