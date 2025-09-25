import type {
  PartialUserProfileRequest,
  UserProfileResponse,
} from '../../../pages/editProfile/types/userProfile';

export const USER_PROFILE: UserProfileResponse = {
  name: '홍길동',
  gender: '남',
  phoneNumber: '010-1234-5678',
  image: null,
};

export const BASE_UPDATED_USER_PROFILE: PartialUserProfileRequest = {
  name: '신종욱',
  gender: '남',
};
