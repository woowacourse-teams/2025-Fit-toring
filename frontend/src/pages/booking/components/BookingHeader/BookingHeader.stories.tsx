import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';

import BookingHeader from './BookingHeader';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Booking/BookingHeader',
  component: BookingHeader,
  parameters: {},

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.BOOKING]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof BookingHeader>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: ' 예약 신청페이지 헤더입니다.',
      },
    },
  },
};
