export type Gender = '남' | '여';

export interface IdentityVerificationInfo {
  name: string;
  gender: Gender;
  phone: string;
}
