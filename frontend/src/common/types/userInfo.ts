import type { GenderClient, GenderServer } from './gender';

export type MemberRole = 'MENTEE' | 'MENTOR';

interface UserInfo<TGender> {
  loginId: string;
  name: string;
  gender: TGender;
  phoneNumber: string;
  image: string | null;
  myRole: MemberRole;
}

export type UserInfoClient = UserInfo<GenderClient>;

export type UserInfoServer = UserInfo<GenderServer>;
