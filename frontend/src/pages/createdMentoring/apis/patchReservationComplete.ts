import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export const patchReservationComplete = async (reservationId: number) => {
  return await apiClient.patch({
    endpoint: `${API_ENDPOINTS.RESERVATIONS}/${reservationId}${API_ENDPOINTS.PATCH_MENTORING_COMPLETE}`,
    body: {},
    withCredentials: true,
  });
};
