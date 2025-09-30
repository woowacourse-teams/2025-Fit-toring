import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export const postKakaoLogin = async (code: string) => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.KAKAO_LOGIN,
    body: { code },
    withCredentials: true,
  });
};
