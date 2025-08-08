import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';
import { StatusTypeEnum } from '../../../../common/types/statusType';

import PhoneNumber from './PhoneNumber';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'CreatedMentoring/PhoneNumber',
  component: PhoneNumber,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.CREATED_MENTORING]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof PhoneNumber>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    status: StatusTypeEnum.approved,
    phoneNumber: '010-1234-5678',
  },
  parameters: {
    docs: {
      description: {
        story:
          'PhoneNumber 컴포넌트는 멘토링 상담 신청의 상태에 따라 연락처를 보여주는 컴포넌트입니다. 상태가 "승인됨, 완료됨"일 때만 연락처가 표시됩니다.',
      },
    },
  },
};
