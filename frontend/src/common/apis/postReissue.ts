import { API_ENDPOINTS } from '../constants/apiEndpoints';

import { apiClient } from './apiClient';

export const postReissue = async () => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.REISSUE,
    body: {},
    withCredentials: true,
  });
};
