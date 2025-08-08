import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';
import { StatusTypeEnum } from '../../../../common/types/statusType';

import ActionButtons from './ActionButtons';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'CreatedMentoring/ActionButtons',
  component: ActionButtons,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.CREATED_MENTORING]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof ActionButtons>;

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
          'ActionButtons 컴포넌트는 멘토링 상담 신청의 상태에 따라 승인 및 거절 버튼을 보여주는 컴포넌트입니다. 상태가 "승인대기"일 때만 버튼이 표시됩니다.',
      },
    },
  },
};
