import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';
import { convertGenderClientToServer } from '../../../common/utils/genderConverter';

import type { IdentityVerificationInfo } from '../components/types/IdentityVerificationInfo';

export const postIdentityVerification = async (
  userInfo: IdentityVerificationInfo,
) => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.IDENTITY_VERIFICATION,
    body: {
      ...userInfo,
      gender: convertGenderClientToServer(userInfo.gender),
      phoneNumber: userInfo.phone,
    },
    withCredentials: true,
  });
};
