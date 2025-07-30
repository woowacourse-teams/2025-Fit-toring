import type { MentorInformation } from '../../../pages/home/types/MentorInformation';

export const MENTORINGS: MentorInformation[] = [
  {
    id: 1,
    mentorName: '김트레이너',
    categories: ['근력 증진', '다이어트', '체형 교정'],
    price: 5000,
    career: 5,
    imageUrl: '',
    introduction:
      '5년차 전문 트레이너로 개인 맞춤 운동 및 식단 코칭을 제공합니다.',
  },
  {
    id: 2,
    mentorName: '이영양사',
    categories: ['영양 상담'],
    price: 4500,
    career: 3,
    imageUrl: '',
    introduction:
      '3년차 전문 영양사로 개인 맞춤 운동 및 식단 코칭을 제공합니다.',
  },
  {
    id: 3,
    mentorName: '이영양사',
    categories: ['영양 상담'],
    price: 4500,
    career: 3,
    imageUrl: null,
    introduction:
      '3년차 전문 영양사로 개인 맞춤 운동 및 식단 코칭을 제공합니다.',
  },
] as const;
