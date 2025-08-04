import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

interface SignupInfoParams {
  loginId: string;
  name: string;
  gender: '남' | '여';
  phone: string;
  password: string;
}

export const postSignup = async (signupInfo: SignupInfoParams) => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.SIGNUP,
    searchParams: { ...signupInfo },
  });
};
