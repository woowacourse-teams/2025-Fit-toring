import { useState, useEffect } from 'react';

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
  const [myProfile, setMyProfile] = useState<UserProfileResponse | null>(null);

  useEffect(() => {
    const fetchMyProfile = async () => {
      try {
        const response = await getUserInfo();

        setMyProfile(convertResponse(response));
      } catch (error) {
        console.error('회원 정보 불러오기 실패', error);
        setMyProfile(null);
      }
    };
    fetchMyProfile();
  }, []);

  return { myProfile };
};

export default useMyProfile;
