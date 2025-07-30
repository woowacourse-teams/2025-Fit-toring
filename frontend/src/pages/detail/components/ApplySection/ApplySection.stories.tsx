import { ThemeProvider } from '@emotion/react';
import { BrowserRouter } from 'react-router-dom';

import { THEME } from '../../../../common/styles/theme';

import ApplySection from './ApplySection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Detail/ApplySection',
  component: ApplySection,

  decorators: [
    (Story) => (
      <BrowserRouter>
        <ThemeProvider theme={THEME}>
          <Story />
        </ThemeProvider>
      </BrowserRouter>
    ),
  ],
} satisfies Meta<typeof ApplySection>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    price: 5000,
  },
  parameters: {
    docs: {
      description: {
        story: `상담 신청 섹션입니다. 사용자가 상담을 신청할 수 있는 버튼과 상담료 정보를 표시합니다.`,
      },
    },
  },
};
