import type { GenderClient } from './gender';

export interface UserInfo {
  loginId: string;
  name: string;
  gender: GenderClient;
  phoneNumber: string;
  image: string | null;
}
