import { apiClient } from '../../../common/apis/apiClient';

import type { Specialty } from '../types/Specialty';

export const getSpecialties = async () => {
  return await apiClient.get<Specialty[]>({
    endpoint: '/categories',
  });
};
