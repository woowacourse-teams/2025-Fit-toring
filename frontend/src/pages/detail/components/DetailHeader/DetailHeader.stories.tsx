import { ThemeProvider } from '@emotion/react';

import MobileLayout from '../../../../common/components/MobileLayout/MobileLayout';
import { THEME } from '../../../../common/styles/theme';

import DetailHeader from './DetailHeader';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Detail/DetailHeader',
  component: DetailHeader,

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
} satisfies Meta<typeof DetailHeader>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultDetailHeader: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'DetailHeader 컴포넌트는 상세 정보 페이지의 헤더를 구성합니다. 뒤로가기 버튼과 제목을 포함하고 있습니다.',
      },
    },
  },
};
