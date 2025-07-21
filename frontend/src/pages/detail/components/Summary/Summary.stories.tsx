import { ThemeProvider } from '@emotion/react';

import MobileLayout from '../../../../common/components/MobileLayout/MobileLayout';
import { THEME } from '../../../../common/styles/theme';

import Summary from './Summary';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Detail/Summary',
  component: Summary,

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
} satisfies Meta<typeof Summary>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultSummary: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'Summary 컴포넌트는 상세 정보 페이지의 요약 정보를 표시합니다. 트레이너의 전문성과 서비스에 대한 간략한 설명을 포함하고 있습니다.',
      },
    },
  },
};
