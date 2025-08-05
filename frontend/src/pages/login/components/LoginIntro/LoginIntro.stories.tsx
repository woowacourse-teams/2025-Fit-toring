import LoginIntro from './LoginIntro';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'login/LoginIntro',
  component: LoginIntro,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof LoginIntro>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: '로그인 페이지의 환영안내 문구입니다.',
      },
    },
  },
};
