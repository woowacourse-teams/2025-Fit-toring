import { ThemeProvider } from '@emotion/react';

import MobileLayout from '../../../../common/components/MobileLayout/MobileLayout';
import { THEME } from '../../../../common/styles/theme';

import MentorOverview from './MentorOverview';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/MentorOverview',
  component: MentorOverview,

  tags: ['autodocs'],

  decorators: [
    (Story) => (
      <ThemeProvider theme={THEME}>
        <MobileLayout>
          <Story />
        </MobileLayout>
      </ThemeProvider>
    ),
  ],
} satisfies Meta<typeof MentorOverview>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    mentorCount: 6,
  },
  parameters: {
    docs: {
      description: {
        story:
          'MentorOverview 컴포넌트는 홈페이지의 중단에 위치하여 사용자에게 멘토의 수와 서비스 개요를 전달합니다.',
      },
    },
  },
};
