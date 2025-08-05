import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export const postAuthCodeVerify = async (phone: string, code: string) => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.AUTH_CODE_VERIFY,
    searchParams: { phone, code },
  });
};
