import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { MentorInformation } from '../types/MentorInformation';

interface GetMentorListParams {
  params: Record<string, string>;
}

export const getMentorList = async ({ params }: GetMentorListParams) => {
  return await apiClient.get<MentorInformation[]>({
    endpoint: API_ENDPOINTS.MENTORINGS,
    searchParams: params,
  });
};
