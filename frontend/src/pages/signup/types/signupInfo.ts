import type { GenderClient } from '../../../common/types/gender';

export interface SignupInfo {
  loginId: string;
  name: string;
  gender: GenderClient;
  phoneNumber: string;
  password: string;
}
