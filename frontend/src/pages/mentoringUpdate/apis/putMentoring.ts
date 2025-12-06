import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { MentoringUpdateFormData } from '../types/mentoringUpdateForm';

type CertificateInfoWithoutId = Omit<
  MentoringUpdateFormData['certificateInfos'][number],
  'id'
>;

interface MentoringUpdateFormDataWithoutId
  extends Omit<MentoringUpdateFormData, 'certificateInfos'> {
  certificateInfos: CertificateInfoWithoutId[];
}

interface PutMentoringParams {
  mentoringData: MentoringUpdateFormDataWithoutId;

  mentoringId: string;
}

export const putMentoring = async ({
  mentoringData,

  mentoringId,
}: PutMentoringParams) => {
  return await apiClient.put({
    endpoint: `${API_ENDPOINTS.MENTORINGS}/${mentoringId}`,
    body: { ...mentoringData },
    withCredentials: true,
  });
};
