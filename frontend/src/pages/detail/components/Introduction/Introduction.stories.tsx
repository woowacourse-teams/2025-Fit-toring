import { ThemeProvider } from '@emotion/react';

import MobileLayout from '../../../../common/components/MobileLayout/MobileLayout';
import { THEME } from '../../../../common/styles/theme';

import Introduction from './Introduction';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Detail/Introduction',
  component: Introduction,

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
} satisfies Meta<typeof Introduction>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultIntroduction: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'Introduction 컴포넌트는 상세 정보 페이지의 소개 섹션을 구성합니다. 트레이너의 전문성과 서비스에 대한 자세한 설명을 포함하고 있습니다.',
      },
    },
  },
};
