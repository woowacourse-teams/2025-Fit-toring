import { useQuery } from '@tanstack/react-query';

import { getUserInfo } from '../../../common/apis/getUserInfo';

import type { UserInfo } from '../../../common/types/userInfo';
import type { UserProfileResponse } from '../types/userProfile';

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
  const { data: myProfile } = useQuery({
    queryKey: ['myProfile'],
    queryFn: async () => {
      const response = await getUserInfo();
      return convertResponse(response);
    },
  });

  return { myProfile };
};

export default useMyProfile;
