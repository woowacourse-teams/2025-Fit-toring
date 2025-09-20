import type { UserInfo } from '../../../common/types/userInfo';

export type UserProfileResponse = Omit<UserInfo, 'loginId'>;

type UserProfileRequest = Omit<UserInfo, 'loginId' | 'image'> & {
  password: string;
};

export type PartialUserProfileRequest = Partial<UserProfileRequest>;
