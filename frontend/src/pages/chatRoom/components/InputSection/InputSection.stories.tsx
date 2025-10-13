import InputSection from './InputSection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'ChatRoom/InputSection',
  component: InputSection,
  decorators: [(Story) => <Story />],
} satisfies Meta<typeof InputSection>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    value: '',
    onChange: () => {},
  },
  parameters: {
    docs: {
      description: {
        story: 'InputSection 컴포넌트는 채팅방의 메시지 입력창을 나타냅니다',
      },
    },
  },
};
