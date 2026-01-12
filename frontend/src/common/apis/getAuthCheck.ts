import { API_ENDPOINTS } from '../constants/apiEndpoints';

import { apiClient } from './apiClient';

interface AuthCheckResponse {
  memberId?: number;
}

export const getAuthCheck = async () => {
  return await apiClient.get<AuthCheckResponse>({
    endpoint: `${API_ENDPOINTS.AUTH_CHECK}`,
    withCredentials: true,
  });
};
