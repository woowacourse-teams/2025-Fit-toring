import { apiClient } from '../../../common/apis/apiClient';

import type { MentorInformation } from '../types/MentorInformation';

interface GetMentoringListParams {
  params: Record<string, string>;
}

export const getMentoringList = async ({ params }: GetMentoringListParams) => {
  return await apiClient.get<MentorInformation[]>({
    endpoint: '/mentorings',
    searchParams: params,
  });
};
