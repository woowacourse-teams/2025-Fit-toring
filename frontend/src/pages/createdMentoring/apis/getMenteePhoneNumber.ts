import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { MenteePhoneNumber } from '../types/menteePhoneNumber';

export const getMenteePhoneNumber = async (reservationId: number) => {
  return await apiClient.get<MenteePhoneNumber>({
    endpoint: `${API_ENDPOINTS.RESERVATION}/${reservationId}${API_ENDPOINTS.MENTEE_PHONE_NUMBER}`,
  });
};
