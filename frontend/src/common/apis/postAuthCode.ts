import { API_ENDPOINTS } from '../constants/apiEndpoints';

import { apiClient } from './apiClient';

export const postAuthCode = async (phone: string) => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.AUTH_CODE,
    body: { phoneNumber: phone },
  });
};
