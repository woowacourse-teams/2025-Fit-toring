import { apiClient } from '../../../common/apis/apiClient';
import { PAGE_URL } from '../../../common/constants/url';

import type { MyProfileResponse } from '../types/myProfile';

export const getMyProfile = async () => {
  // TODO: common에 있는 getUserInfo를 사용하도록 변경
  // TODO: mock/myProfile/handlers 및 data도 수정
  const { data } = await apiClient.get<MyProfileResponse>({
    endpoint: PAGE_URL.MY_PAGE,
    withCredentials: true,
  });

  return data;
};
