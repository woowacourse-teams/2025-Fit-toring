import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export const postKakaoLogin = async (authCode: string) => {
  const redirectUri = encodeURIComponent(process.env.KAKAO_REDIRECT_URI!);
  return await apiClient.post({
    endpoint: `${API_ENDPOINTS.KAKAO_LOGIN}?code=${encodeURIComponent(authCode)}&redirectUrl=${redirectUri}`,
    withCredentials: true,
  });
};
