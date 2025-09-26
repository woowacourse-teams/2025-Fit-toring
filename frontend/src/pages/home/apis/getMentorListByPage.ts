import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { MentoringByPage } from '../types/MentoringByPage';

interface GetMentorListByPageParams {
  params: Record<string, string>;
}

export const getMentorListByPage = async ({
  params,
}: GetMentorListByPageParams) => {
  return await apiClient.get<MentoringByPage>({
    endpoint: API_ENDPOINTS.MENTORINGS_PAGE,
    searchParams: params,
  });
};
