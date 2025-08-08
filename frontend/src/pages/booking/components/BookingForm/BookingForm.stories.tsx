import BookingForm from './BookingForm';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'booking/BookingForm',
  component: BookingForm,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof BookingForm>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    handleBookingButtonClick: () => {},
    mentoringId: 1,
  },
  parameters: {
    docs: {
      description: {
        story: '예약신청 페이지의 신청폼입니다.',
      },
    },
  },
};
