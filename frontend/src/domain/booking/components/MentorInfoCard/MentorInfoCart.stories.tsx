import { ThemeProvider } from '@emotion/react';

import { THEME } from '../../../../common/styles/theme';

import MentoInfoCard from './MentorInfoCard';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'booking/MentoInfoCard',
  component: MentoInfoCard,
  tags: ['autodocs'],
  decorators: [
    (Story) => (
      <ThemeProvider theme={THEME}>
        <Story />
      </ThemeProvider>
    ),
  ],
} satisfies Meta<typeof MentoInfoCard>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: '기본 예약페이지의 멘토 정보 카드입니다.',
      },
    },
  },
};
