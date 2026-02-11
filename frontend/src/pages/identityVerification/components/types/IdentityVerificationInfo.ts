import type { GenderClient } from '../../../../common/types/gender';

export interface IdentityVerificationInfo {
  name: string;
  gender: GenderClient;
  phone: string;
}
