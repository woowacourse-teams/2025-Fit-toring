export type Gender = '남' | '여';

export interface SignupInfo {
  loginId: string;
  name: string;
  gender: Gender;
  phone: string;
  password: string;
}
