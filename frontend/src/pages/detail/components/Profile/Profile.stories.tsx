import { ThemeProvider } from '@emotion/react';

import MobileLayout from '../../../../common/components/MobileLayout/MobileLayout';
import { THEME } from '../../../../common/styles/theme';

import Profile from './Profile';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Detail/Profile',
  component: Profile,

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
} satisfies Meta<typeof Profile>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultProfile: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'Profile 컴포넌트는 상세 정보 페이지의 프로필 섹션을 구성합니다. 트레이너의 이름, 평점, 카테고리 태그 등을 포함하고 있습니다.',
      },
    },
  },
};
