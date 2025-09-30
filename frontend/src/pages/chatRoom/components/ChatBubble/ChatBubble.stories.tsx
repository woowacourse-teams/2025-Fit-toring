import ChatBubble from './ChatBubble';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'ChatRoom/ChatBubble',
  component: ChatBubble,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof ChatBubble>;

export default meta;
type Story = StoryObj<typeof meta>;

export const SendBubble: Story = {
  args: {
    content: '안녕하세요 반갑습니다.',
    createdAt: '2025-09-27T14:35:03',
    authored: true,
  },
  parameters: {
    docs: {
      description: {
        story:
          'ChatBubble 컴포넌트는 채팅방의 입력된 말풍선?을 나타냅니다. 내가 작성한 말풍선은 왼쪽에 배치됩니다.',
      },
    },
  },
};

export const ReceiveBubble: Story = {
  args: {
    content: '안녕하세요 반갑습니다.',
    createdAt: '2025-09-27T14:35:03',
    authored: false,
  },
  parameters: {
    docs: {
      description: {
        story:
          'ChatBubble 컴포넌트는 채팅방의 입력된 말풍선?을 나타냅니다. 상대가 작성한 말풍선은 오른쪽에 배치됩니다.',
      },
    },
  },
};
