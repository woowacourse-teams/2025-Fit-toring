import SignupIntro from './SignupIntro';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'signup/SignupIntro',
  component: SignupIntro,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof SignupIntro>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: '회원가입 페이지의 환영안내 문구입니다.',
      },
    },
  },
};
