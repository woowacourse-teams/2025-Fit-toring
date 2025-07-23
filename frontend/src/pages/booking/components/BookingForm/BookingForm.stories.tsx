import { ThemeProvider } from '@emotion/react';

import { THEME } from '../../../../common/styles/theme';

import BookingForm from './BookingForm';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'booking/BookingForm',
  component: BookingForm,
  tags: ['autodocs'],
  decorators: [
    (Story) => (
      <ThemeProvider theme={THEME}>
        <Story />
      </ThemeProvider>
    ),
  ],
} satisfies Meta<typeof BookingForm>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: '예약신청 페이지의 신청폼입니다.',
      },
    },
  },
};
