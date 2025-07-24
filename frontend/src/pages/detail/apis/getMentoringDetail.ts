import { apiClient } from '../../../common/apis/apiClient';

import type { Mentoring } from '../types/Mentoring';

export const getMentoringDetail = async (mentoringId: string) => {
  return await apiClient.get<Mentoring>({
    endpoint: `/mentorings/${mentoringId}`,
  });
};
