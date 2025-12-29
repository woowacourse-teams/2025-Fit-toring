import { useQuery } from '@tanstack/react-query';

import { getUserInfo } from '../../../common/apis/getUserInfo';

import type { UserInfoClient } from '../../../common/types/userInfo';
import type { UserProfileResponse } from '../types/userProfile';

export const MY_PROFILE_QUERY_KEY = {
  myProfile: (key: string | null) => ['myProfile', key],
} as const;

const convertResponse = (response: UserInfoClient): UserProfileResponse => {
  const { name, gender, phoneNumber, image } = response;
  return {
    name,
    gender,
    phoneNumber,
    image,
  };
};

const useMyProfile = () => {
  const memberId = localStorage.getItem('memberId');

  const { data: myProfile } = useQuery({
    queryKey: MY_PROFILE_QUERY_KEY.myProfile(memberId),
    queryFn: async () => {
      const response = await getUserInfo();
      return convertResponse(response);
    },
  });

  return { myProfile };
};

export default useMyProfile;
