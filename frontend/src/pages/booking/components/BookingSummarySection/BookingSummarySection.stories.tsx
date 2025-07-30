import { ThemeProvider } from '@emotion/react';

import { THEME } from '../../../../common/styles/theme';

import BookingSummarySection from './BookingSummarySection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'booking/BookingSummarySection',
  component: BookingSummarySection,

  decorators: [
    (Story) => (
      <ThemeProvider theme={THEME}>
        <Story />
      </ThemeProvider>
    ),
  ],
} satisfies Meta<typeof BookingSummarySection>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: '예약신청 페이지의 신청하기 버튼 섹션입니다.',
      },
    },
  },
};
