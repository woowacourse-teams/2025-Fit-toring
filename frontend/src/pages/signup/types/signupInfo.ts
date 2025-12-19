import type { GenderClient } from '../../../common/types/gender';

export interface SignupInfo {
  loginId: string;
  name: string;
  gender: GenderClient;
  phone: string;
  password: string;
}
