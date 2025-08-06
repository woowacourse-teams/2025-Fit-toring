import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { SignupInfo } from '../types/signupInfo';

export const postSignup = async (signupInfo: SignupInfo) => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.SIGNUP,
    body: { ...signupInfo },
  });
};
