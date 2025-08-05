import { API_ENDPOINTS } from '../constants/apiEndpoints';

import { apiClient } from './apiClient';

import type { Specialty } from '../types/Specialty';

export const getSpecialties = async () => {
  return await apiClient.get<Specialty[]>({
    endpoint: API_ENDPOINTS.SPECIALTIES,
  });
};
