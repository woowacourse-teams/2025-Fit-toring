import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';
import { convertGenderClientToServer } from '../../../common/utils/genderConverter';

import type { PartialUserProfileRequest } from '../types/userProfile';

export const patchMyProfile = async (body: PartialUserProfileRequest) => {
  return await apiClient.patch({
    endpoint: API_ENDPOINTS.MEMBERS_ME,
    body: {
      ...body,
      gender: body.gender && convertGenderClientToServer(body.gender),
    },
    withCredentials: true,
  });
};
