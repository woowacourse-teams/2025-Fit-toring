import type { MentorInformation } from '../../../pages/home/types/MentorInformation';

export const MENTORINGS: MentorInformation[] = [
  {
    id: 1,
    mentorName: '김트레이너',
    categories: ['근력 운동', '다이어트', '체형 교정'],
    price: 5000,
    career: 5,
    profileImageUrl: '',
    introduction:
      '5년차 전문 트레이너로 개인 맞춤 운동 및 식단 코칭을 제공합니다.',
    ratingAverage: '4.5',
    ratingCount: 127,
  },
  {
    id: 2,
    mentorName: '이영양사',
    categories: ['영양 상담'],
    price: 4500,
    career: 3,
    profileImageUrl: '',
    introduction:
      '3년차 전문 영양사로 개인 맞춤 운동 및 식단 코칭을 제공합니다.',
    ratingAverage: '4.2',
    ratingCount: 237,
  },

  {
    id: 3,
    mentorName: '이영양사',
    categories: ['영양 상담'],
    price: 4500,
    career: 3,
    profileImageUrl: null,
    introduction:
      '3년차 전문 영양사로 개인 맞춤 운동 및 식단 코칭을 제공합니다.',
    ratingAverage: '4.8',
    ratingCount: 122,
  },
] as const;
