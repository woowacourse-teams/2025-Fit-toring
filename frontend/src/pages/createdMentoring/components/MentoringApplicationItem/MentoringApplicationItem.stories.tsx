import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';
import { MENTORING_APPLICATIONS } from '../../../../common/mock/createdMentoring/data';

import MentoringApplicationItem from './MentoringApplicationItem';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'CreatedMentoring/MentoringApplicationItem',
  component: MentoringApplicationItem,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.CREATED_MENTORING]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof MentoringApplicationItem>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    mentoringApplication: MENTORING_APPLICATIONS[0],
  },
  parameters: {
    docs: {
      description: {
        story:
          'MentoringApplicationItem 컴포넌트는 멘토링 상담 신청 정보 카드입니다. 멘토링 상담 신청 정보를 보여주고, 상태에 따라 승인, 거절 버튼을 표시합니다. 상태에 따라 아이콘과 스타일이 변경됩니다.',
      },
    },
  },
};

export const Pending: Story = {
  args: {
    mentoringApplication: MENTORING_APPLICATIONS[0],
  },
  parameters: {
    docs: {
      description: {
        story:
          '승인 대기중인 멘토링 상담 신청 정보 카드입니다. 상태가 "승인대기"으로 표시되며, 연락처가 표시됩니다. 승인 대기중인 상담 신청 정보를 보여줍니다.',
      },
    },
  },
};

export const Approved: Story = {
  args: {
    mentoringApplication: MENTORING_APPLICATIONS[1],
  },
  parameters: {
    docs: {
      description: {
        story:
          '승인된 멘토링 상담 신청 정보 카드입니다. 상태가 "승인됨"으로 표시되며, 연락처가 표시됩니다. 승인된 상담 신청 정보를 보여줍니다.',
      },
    },
  },
};
export const Completed: Story = {
  args: {
    mentoringApplication: MENTORING_APPLICATIONS[2],
  },
  parameters: {
    docs: {
      description: {
        story:
          '완료된 멘토링 상담 신청 정보 카드입니다. 상태가 "완료됨"으로 표시되며, 연락처가 표시됩니다. 완료된 상담 신청 정보를 보여줍니다.',
      },
    },
  },
};
