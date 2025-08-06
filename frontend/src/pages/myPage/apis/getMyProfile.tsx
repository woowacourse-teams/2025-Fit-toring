import { apiClient } from '../../../common/apis/apiClient';
import { PAGE_URL } from '../../../common/constants/url';

import type { MyProfileResponse } from '../types/myProfile';

export const getMyProfile = async () => {
  const { data } = await apiClient.get<MyProfileResponse>({
    endpoint: PAGE_URL.MY_PAGE,
  });

  return data;
};
