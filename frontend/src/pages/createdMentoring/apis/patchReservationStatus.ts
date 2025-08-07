import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { MENTORING_APPLICATION_STATUS } from '../types/mentoringApplicationStatus';

export const patchReservationStatus = async (
  reservationId: number,
  searchParams: { status: MENTORING_APPLICATION_STATUS },
) => {
  return await apiClient.patch({
    endpoint: `${API_ENDPOINTS.RESERVATION}/${reservationId}${API_ENDPOINTS.PATCH_MENTORING_STATUS}`,
    searchParams,
    withCredentials: true,
  });
};
