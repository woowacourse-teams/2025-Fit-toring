import { API_ENDPOINTS } from '../constants/apiEndpoints';

import { apiClient } from './apiClient';

export const postAuthCodeVerify = async (phone: string, code: string) => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.AUTH_CODE_VERIFY,
    body: { phoneNumber: phone, code },
  });
};
