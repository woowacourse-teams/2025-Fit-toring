import type { UserInfoClient } from '../../../common/types/userInfo';

export type UserProfileResponse = Omit<UserInfoClient, 'loginId'>;

type UserProfileRequest = Omit<UserInfoClient, 'loginId' | 'image'> & {
  password: string;
};

export type PartialUserProfileRequest = Partial<UserProfileRequest>;
