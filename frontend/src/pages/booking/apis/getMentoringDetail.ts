import { apiClient } from '../../../common/apis/apiClient';

export interface MentoringDetail {
  id: number;
  mentorName: string;
  categories: string[];
  price: number;
  career: number;
  imageUrl: string;
  introduction: string;
}

export const getMentoringDetail = async (
  mentoringId: string,
): Promise<MentoringDetail> => {
  return await apiClient.get({ endpoint: `/mentorings/${mentoringId}` });
};
