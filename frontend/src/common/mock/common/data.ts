import type { Specialty } from '../../types/Specialty';
import type { UserInfo } from '../../types/userInfo';

export const SPECIALTIES: Specialty[] = [
  { id: '1', title: '근력 운동' },
  { id: '2', title: '유산소 운동' },
  { id: '3', title: '다이어트' },
  { id: '4', title: '체형 교정' },
  { id: '5', title: '영양 상담' },
  { id: '6', title: '재활 운동' },
] as const;

export const USER_INFO: UserInfo = {
  loginId: 1,
  name: '홍길동',
  gender: '남',
  phone: '010-1234-5678',
  image: null,
};
