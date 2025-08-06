import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { mentoringCreateFormData } from '../components/types/mentoringCreateFormData';

export const postMentoringCreate = async (
  mentoringData: mentoringCreateFormData,
  profileImageFile: File | null,
  certificateImageFiles: File[],
) => {
  const formData = new FormData();
  formData.append('data', JSON.stringify(mentoringData));
  if (profileImageFile) {
    formData.append('image', profileImageFile);
  }
  certificateImageFiles.forEach((file) =>
    formData.append('certificateImages', file),
  );

  return await apiClient.post({
    endpoint: API_ENDPOINTS.MENTORINGS,
    body: formData,
  });
};
