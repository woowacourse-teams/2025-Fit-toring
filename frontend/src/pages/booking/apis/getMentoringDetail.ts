import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export interface MentoringDetail {
  id: number;
  mentorName: string;
  categories: string[];
  price: number;
  career: number;
  imageUrl: string;
  introduction: string;
}

export const getMentoringDetail = async (mentoringId: string) => {
  return await apiClient.get<MentoringDetail>({
    endpoint: `${API_ENDPOINTS.MENTORINGS}/${mentoringId}`,
  });
};
