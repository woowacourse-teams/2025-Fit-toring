import type { MentoringApplication } from '../../../pages/createdMentoring/types/mentoringApplication';

export const MENTORING_APPLICATIONS: MentoringApplication[] = [
  {
    id: 1,
    name: '홍길동',
    phoneNumber: null,
    price: 5000,
    content:
      '다이어트를 위한 운동 계획과 식단 관리에 대해 상담받고 싶습니다. 현재 직장인이라 시간이 제한적인데, 효율적인 운동 방법을 알고 싶어요.',
    status: '승인대기',
    applicationDate: '2025-01-15',
    scheduledDate: null,
    completionDate: null,
  },
  {
    id: 2,
    name: '김영희',
    phoneNumber: '010-2345-6789',
    price: 5000,
    content:
      '근육 증가를 위한 식단과 운동 계획에 대해 상담받고 싶습니다. 평일에 짧게 운동할 시간이 있어 효율적인 방법을 찾고 싶어요.',
    status: '승인됨',
    applicationDate: '2025-01-14',
    scheduledDate: '2025-01-21',
    completionDate: null,
  },
  {
    id: 3,
    name: '박철수',
    phoneNumber: '010-3456-7890',
    price: 5000,
    content: '헬스장에서 운동하고 있는데 정체기가 와서 도움이 필요해요.',
    status: '완료됨',
    applicationDate: '2025-01-12',
    scheduledDate: '2025-01-18',
    completionDate: '2025-01-18',
  },
  {
    id: 4,
    name: '이순신',
    phoneNumber: '010-4567-8901',
    price: 5000,
    content:
      '체중 감량을 위한 운동과 식단 조절에 대해 상담받고 싶습니다. 현재 체중이 많이 나가서 걱정이에요.',
    status: '승인대기',
    applicationDate: '2025-01-10',
    scheduledDate: null,
    completionDate: null,
  },
  {
    id: 5,
    name: '최영',
    phoneNumber: '010-5678-9012',
    price: 5000,
    content:
      '근력 운동과 유산소 운동을 병행하는 방법에 대해 상담받고 싶습니다. 현재 운동을 시작한 지 얼마 안 돼서 조언이 필요해요.',
    status: '승인됨',
    applicationDate: '2025-01-08',
    scheduledDate: '2025-01-15',
    completionDate: null,
  },
] as const;
