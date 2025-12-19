import type { GenderClient, GenderServer } from './gender';

interface UserInfo<TGender> {
  loginId: string;
  name: string;
  gender: TGender;
  phoneNumber: string;
  image: string | null;
}

export type UserInfoClient = UserInfo<GenderClient>;

export type UserInfoServer = UserInfo<GenderServer>;
