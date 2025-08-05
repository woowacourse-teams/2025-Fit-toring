import { API_ENDPOINTS } from '../constants/apiEndpoints';

import { apiClient } from './apiClient';

import { UserInfo } from '../types/userInfo';

export const getUserInfo = async () => {
  return await apiClient.get<UserInfo>({
    endpoint: API_ENDPOINTS.MEMBERS_ME,
  });
};
