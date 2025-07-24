import { ThemeProvider } from '@emotion/react';

import MobileLayout from '../../../../common/components/MobileLayout/MobileLayout';
import { THEME } from '../../../../common/styles/theme';

import MentorSummary from './MentorSummary';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Detail/MentorSummary',
  component: MentorSummary,

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
} satisfies Meta<typeof MentorSummary>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultMentorSummary: Story = {
  args: {
    introduction:
      '안녕하세요 김트레이너 입니다. 여러분의 건강과 체력을 책임지겠습니다.',
    career: 5,
  },
  parameters: {
    docs: {
      description: {
        story:
          'MentorSummary 컴포넌트는 상세 정보 페이지의 요약 정보를 표시합니다. 트레이너의 전문성과 서비스에 대한 간략한 설명을 포함하고 있습니다.',
      },
    },
  },
};
