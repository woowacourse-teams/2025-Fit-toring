import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { mentoringCreateFormData } from '../../../common/types/mentoringCreateFormData';

export const postMentoringCreate = async ({
  mentoringData,
  profileImageFile,
  certificateImageFiles,
}: {
  mentoringData: mentoringCreateFormData;
  profileImageFile: File | null;
  certificateImageFiles: File[];
}) => {
  const formData = new FormData();

  const jsonBlob = new Blob([JSON.stringify(mentoringData)], {
    type: 'application/json',
  });
  formData.append('data', jsonBlob);

  if (profileImageFile) {
    formData.append('image', profileImageFile);
  }
  certificateImageFiles.forEach((file) =>
    formData.append('certificateImages', file),
  );

  return await apiClient.post({
    endpoint: API_ENDPOINTS.MENTORINGS,
    body: formData,
    withCredentials: true,
  });
};
