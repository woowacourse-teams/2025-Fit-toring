import type { MentoringDetail } from '../../types/MentoringDetail';

export const MENTORING_DETAIL: MentoringDetail = {
  id: 1,
  mentorName: '김트레이너',
  ratingAverage: ' 3.7',
  ratingCount: 10,
  categories: ['근력 증진', '다이어트', '체형 교정'],
  price: 5000,
  career: 5,
  profileImageUrl: null,
  introduction:
    '5년차 전문 트레이너로 개인 맞춤 운동 및 식단 코칭을 제공합니다.',
  content:
    '김트레이너는 대단합니다. 저를 믿고 신청해주세요. 모든것을 도와드립니다. 여러분 저를 믿어주세요.',
  chatUrl: '',
  certificates: [
    {
      certificateId: '1',
      title: '스포츠안마 자격증',
      type: 'LICENSE',
      imageUrl: '',
    },
    {
      certificateId: '2',
      title: '한국대학교 졸업증명서',
      type: 'EDUCATION',
      imageUrl: '',
    },
    {
      certificateId: '3',
      title: '헬스 트레이너 자격증',
      type: 'LICENSE',
      imageUrl: '',
    },
    {
      certificateId: '4',
      title: 'PT 자격증',
      type: 'LICENSE',
      imageUrl: '',
    },
    {
      certificateId: '5',
      title: '요가 자격증',
      type: 'LICENSE',
      imageUrl: '',
    },
    {
      certificateId: '6',
      title: '필라테스 자격증',
      type: 'LICENSE',
      imageUrl: '',
    },
  ],
};
