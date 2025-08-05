import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export const postAuthCode = async (phone: string) => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.AUTH_CODE,
    body: { phone },
  });
};
