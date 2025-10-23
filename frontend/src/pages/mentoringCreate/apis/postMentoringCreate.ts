import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { mentoringCreateFormData } from '../../../common/types/mentoringCreateFormData';

export const postMentoringCreate = async ({
  mentoringData,
}: {
  mentoringData: mentoringCreateFormData;
}) => {
  return await apiClient.post<mentoringCreateFormData>({
    endpoint: API_ENDPOINTS.MENTORINGS,
    body: { ...mentoringData },
    withCredentials: true,
  });
};
