import { ThemeProvider } from '@emotion/react';

import MobileLayout from '../../../../common/components/MobileLayout/MobileLayout';
import { THEME } from '../../../../common/styles/theme';

import SpecialtyFilterModalButton from './SpecialtyFilterModalButton';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/SpecialtyFilterModalButton',
  component: SpecialtyFilterModalButton,

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
} satisfies Meta<typeof SpecialtyFilterModalButton>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    handleOpenModal: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          'SpecialtyFilterModalButton 컴포넌트는 홈페이지의 중단에 위치하여 전문 분야 필터링을 할 수 있는 모달을 열기 위한 버튼입니다.',
      },
    },
  },
};
