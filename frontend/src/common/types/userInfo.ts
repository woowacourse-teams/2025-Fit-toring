import type { Gender } from '../../pages/signup/types/signupInfo';

export interface UserInfo {
  loginId: string;
  name: string;
  gender: Gender;
  phoneNumber: string;
  image: string | null;
}
