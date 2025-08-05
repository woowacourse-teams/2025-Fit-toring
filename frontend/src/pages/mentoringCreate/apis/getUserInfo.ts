import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { UserInfoResponse } from '../components/types/userInfoResponse';

export const getUserInfo = async () => {
  return await apiClient.get<UserInfoResponse>({
    endpoint: API_ENDPOINTS.MEMBERS,
  });
};
