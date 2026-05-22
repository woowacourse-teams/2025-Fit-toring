import type { UserInfoClient } from '../../../common/types/userInfo';

export type UserProfileResponse = Omit<UserInfoClient, 'loginId'>;

type UserProfileRequest = Pick<
  UserInfoClient,
  'name' | 'gender' | 'phoneNumber'
> & {
  password: string;
  profileImageKey: string;
};

export type PartialUserProfileRequest = Partial<UserProfileRequest>;
