import { StatusTypeEnum } from '../../../types/statusType';

import MentoringStepper from './MentoringStepper';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Common/MentoringStepper',
  component: MentoringStepper,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof MentoringStepper>;

export default meta;
type Story = StoryObj<typeof meta>;

export const PendingStep: Story = {
  args: {
    status: StatusTypeEnum.PENDING,
  },
  parameters: {
    docs: {
      description: {
        story:
          '멘토링 예약을 신청한 단계로, 사용자가 예약을 제출했지만 아직 확정되지 않은 상태를 나타냅니다.',
      },
    },
  },
};

export const ApprovedStep: Story = {
  args: {
    status: StatusTypeEnum.APPROVED,
  },
  parameters: {
    docs: {
      description: {
        story:
          '멘토가 예약을 승인하여 신청이 확정된 단계입니다. 다음 단계인 멘토링 완료로 진행될 수 있습니다.',
      },
    },
  },
};

export const CompletedStep: Story = {
  args: {
    status: StatusTypeEnum.COMPLETE,
  },
  parameters: {
    docs: {
      description: {
        story: '멘토링이 정상적으로 완료된 최종 단계입니다.',
      },
    },
  },
};
