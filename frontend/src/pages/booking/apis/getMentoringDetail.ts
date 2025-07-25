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

export const getMentoringDetail = async (mentoringId: string) => {
  return await apiClient.get<MentoringDetail>({
    endpoint: `/mentorings/${mentoringId}`,
  });
};
