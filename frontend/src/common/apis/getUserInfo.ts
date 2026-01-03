import { API_ENDPOINTS } from '../constants/apiEndpoints';
import { convertGenderServerToClient } from '../utils/genderConverter';

import { apiClient } from './apiClient';

import type { UserInfoServer } from '../types/userInfo';

export const getUserInfo = async () => {
  const response = await apiClient.get<UserInfoServer>({
    endpoint: API_ENDPOINTS.MEMBERS_ME,
    withCredentials: true,
  });

  return {
    ...response,
    gender: convertGenderServerToClient(response.gender),
  };
};
