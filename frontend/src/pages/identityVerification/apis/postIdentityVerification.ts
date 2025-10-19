import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { IdentityVerificationInfo } from '../components/types/IdentityVerificationInfo';

export const postIdentityVerification = async (
  userInfo: IdentityVerificationInfo,
) => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.IDENTITY_VERIFICATION,
    body: { ...userInfo },
    withCredentials: true,
  });
};
