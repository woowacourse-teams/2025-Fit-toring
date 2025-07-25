import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { Specialty } from '../types/Specialty';

export const getSpecialties = async () => {
  return await apiClient.get<Specialty[]>({
    endpoint: API_ENDPOINTS.SPECIALTIES,
  });
};
