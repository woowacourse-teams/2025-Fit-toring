import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export const postValidateId = async (loginId: string) => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.VALIDATE_ID,
    body: { loginId },
  });
};
