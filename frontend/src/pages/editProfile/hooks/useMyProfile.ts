import { useQuery } from '@tanstack/react-query';

import { getUserInfo } from '../../../common/apis/getUserInfo';

import type { UserInfo } from '../../../common/types/userInfo';
import type { UserProfileResponse } from '../types/userProfile';

export const MY_PROFILE_QUERY_KEY = {
  myProfile: (key: string | null) => ['myProfile', key],
} as const;

const convertResponse = (response: UserInfo): UserProfileResponse => {
  const { name, gender, phoneNumber, image } = response;
  return {
    name,
    gender,
    phoneNumber,
    image,
  };
};

const useMyProfile = () => {
  const storedData = localStorage.getItem('memberId');
  const parsedData = storedData ? JSON.parse(storedData) : null;
  const memberId = parsedData ? parsedData.memberId : null;

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
