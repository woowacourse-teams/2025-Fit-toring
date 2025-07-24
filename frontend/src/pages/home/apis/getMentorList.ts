import { apiClient } from '../../../common/apis/apiClient';

import type { MentorInformation } from '../types/MentorInformation';

interface GetMentorListParams {
  params: Record<string, string>;
}

export const getMentorList = async ({ params }: GetMentorListParams) => {
  return await apiClient.get<MentorInformation[]>({
    endpoint: '/mentorings',
    searchParams: params,
  });
};
