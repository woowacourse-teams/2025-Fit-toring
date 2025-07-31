import SignupForm from './SignupForm';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'signup/SignupForm',
  component: SignupForm,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof SignupForm>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: '회원가입 페이지의 회원가임 폼입니다.',
      },
    },
  },
};
