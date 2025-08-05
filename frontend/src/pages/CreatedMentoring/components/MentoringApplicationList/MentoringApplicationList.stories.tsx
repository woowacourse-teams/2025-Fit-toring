import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';
import MentoringApplicationItem from '../MentoringApplicationItem/MentoringApplicationItem';

import MentoringApplicationList from './MentoringApplicationList';

import type { MentoringApplication } from '../../types/mentoringApplication';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'CreatedMentoring/MentoringApplicationList',
  component: MentoringApplicationList,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.CREATED_MENTORING]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof MentoringApplicationList>;

export default meta;
type Story = StoryObj<typeof meta>;

const MENTORING_APPLICATIONS: MentoringApplication[] = [
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
] as const;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'MentoringApplicationList 컴포넌트는 개설한 멘토링에 대해 받은 멘토링 상담 신청 목록을 보여주는 컴포넌트입니다. 멘토링 신청 정보를 세로로 나열하고 있습니다.',
      },
    },
  },
  args: {
    children: (
      <>
        {MENTORING_APPLICATIONS.map((item) => (
          <MentoringApplicationItem mentoringApplication={item} />
        ))}
      </>
    ),
  },
};
