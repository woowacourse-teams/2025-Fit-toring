import { ThemeProvider } from '@emotion/react';
import { BrowserRouter } from 'react-router-dom';
import { fn } from 'storybook/test';

import { THEME } from '../../../../common/styles/theme';

import CompleteModal from './CompleteModal';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Booking/CompleteModal',
  component: CompleteModal,
  parameters: {},

  tags: ['autodocs'],

  args: { opened: true, onCloseClick: fn() },
  decorators: [
    (Story) => (
      <BrowserRouter>
        <ThemeProvider theme={THEME}>
          <Story />
        </ThemeProvider>
      </BrowserRouter>
    ),
  ],
} satisfies Meta<typeof CompleteModal>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Primary: Story = {
  args: {
    opened: true,
    onCloseClick: fn(),
  },
  parameters: {
    docs: {
      description: {
        story: `예약 완료 모달입니다. 사용자가 예약을 완료하면 이 모달이 표시되고 멘토의 번호를 확인할 수 있습니다.`,
      },
    },
  },
};
