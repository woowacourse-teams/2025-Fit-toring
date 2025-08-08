import { MemoryRouter } from 'react-router-dom';
import { fn } from 'storybook/test';

import { PAGE_URL } from '../../../../common/constants/url';

import CompleteModal from './CompleteModal';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Booking/CompleteModal',
  component: CompleteModal,

  args: { opened: true, onCloseClick: fn() },
  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.BOOKING]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof CompleteModal>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultModal: Story = {
  args: {
    bookedInfo: {
      mentorName: '김트레이너',
      menteeName: '홍길동',
      menteePhone: '010-1111-1111',
    },
    opened: true,
    onCloseClick: fn(),
  },
  parameters: {
    docs: {
      description: {
        story: `예약 완료 모달입니다. 사용자가 예약을 완료하면 이 모달이 표시되고 멘토의 번호를 확인할 수 있습니다.`,
      },
    },
  },
};
