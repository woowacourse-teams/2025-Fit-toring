import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';
import { StatusTypeEnum } from '../../types/statusType';

import MentoringApplicationStatus from './MentoringApplicationStatus';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'CreatedMentoring/MentoringApplicationStatus',
  component: MentoringApplicationStatus,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.CREATED_MENTORING]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof MentoringApplicationStatus>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    status: StatusTypeEnum.pending,
  },
  parameters: {
    docs: {
      description: {
        story:
          'MentoringApplicationStatus 컴포넌트는 멘토링 상담 신청의 상태에 따라 상태를 표시하는 컴포넌트입니다. 상태가 "승인대기, 승인됨, 완료됨"일 때 각각의 상태에 맞는 스타일로 표시됩니다.',
      },
    },
  },
};

export const Approved: Story = {
  args: {
    status: StatusTypeEnum.approved,
  },
  parameters: {
    docs: {
      description: {
        story:
          '멘토링 상담 신청이 승인된 상태를 나타냅니다. 상태가 "승인됨"일 때 표시됩니다.',
      },
    },
  },
};

export const Completed: Story = {
  args: {
    status: StatusTypeEnum.completed,
  },
  parameters: {
    docs: {
      description: {
        story:
          '멘토링 상담 신청이 완료된 상태를 나타냅니다. 상태가 "완료됨"일 때 표시됩니다.',
      },
    },
  },
};
