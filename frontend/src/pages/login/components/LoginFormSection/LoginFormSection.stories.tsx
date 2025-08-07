import { BrowserRouter } from 'react-router-dom';

import LoginFormSection from '../LoginFormSection/LoginFormSection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'login/LoginFormSection',
  component: LoginFormSection,

  decorators: [
    (Story) => (
      <BrowserRouter>
        <Story />
      </BrowserRouter>
    ),
  ],
} satisfies Meta<typeof LoginFormSection>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: '로그인 페이지의 로그인 폼 영역입니다.',
      },
    },
  },
};
