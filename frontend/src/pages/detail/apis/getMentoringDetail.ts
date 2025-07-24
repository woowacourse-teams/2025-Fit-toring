import { apiClient } from '../../../common/apis/apiClient';

import type { MentoringResponse } from '../types/MentoringResponse';

export const getMentoringDetail = async (mentoringId: string) => {
  return await apiClient.get<MentoringResponse>({
    endpoint: `/mentorings/${mentoringId}`,
  });
};
