import { MemoryRouter } from 'react-router-dom';

import AuthFooter from './AuthFooter';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'signup/AuthFooter',
  component: AuthFooter,

  decorators: [
    (Story) => (
      <MemoryRouter>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof AuthFooter>;

export default meta;
type Story = StoryObj<typeof meta>;

export const SignupPageFooter: Story = {
  parameters: {
    docs: {
      description: {
        story:
          '회원가입 페이지 하단에 위치하며 로그인 페이지로 쉽게 이동할 수 있도록 돕습니다.',
      },
    },
  },
  args: {
    currentPage: 'signup',
  },
};

export const LoginPageFooter: Story = {
  parameters: {
    docs: {
      description: {
        story:
          '로그인 페이지 하단에 위치하며 회원가입 페이지로 쉽게 이동할 수 있도록 돕습니다.',
      },
    },
  },
  args: {
    currentPage: 'login',
  },
};
