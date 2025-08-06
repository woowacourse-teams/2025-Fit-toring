import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export const postLogin = async (loginId: string, password: string) => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.LOGIN,
    body: { loginId, password },
    withCredentials: true,
  });
};
