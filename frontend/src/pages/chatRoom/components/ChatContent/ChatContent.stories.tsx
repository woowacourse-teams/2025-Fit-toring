import ChatContent from './ChatContent';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'ChatRoom/ChatContent',
  component: ChatContent,
  decorators: [(Story) => <Story />],
} satisfies Meta<typeof ChatContent>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    messages: [
      {
        content:
          '안녕하세요 회원님 멘토링 시작하겠습니다! 저는 멘토 김멘토입니다 어쩌구 저쩌구',
        createdAt: '2025-09-27T14:35:03',
        senderId: '1',
      },
      {
        content: '어떤 내용이 궁금하실까요?',
        createdAt: '2025-09-27T14:36:03',
        senderId: '1',
      },
      {
        content: '넵 안녕하세요 몇시쯤 어디서 만날까요?',
        createdAt: '2025-09-27T14:37:03',
        senderId: '2',
      },
      {
        content:
          '안녕하세요 회원님 멘토링 시작하겠습니다! 저는 멘토 김멘토입니다 어쩌구 저쩌구',
        createdAt: '2025-09-27T16:35:03',
        senderId: '1',
      },
      {
        content: '어떤 내용이 궁금하실까요?',
        createdAt: '2025-09-27T16:05:03',
        senderId: '1',
      },
    ],
  },
  parameters: {
    docs: {
      description: {
        story:
          'ChatContent 컴포넌트는 상대방과 주고 받은 채팅 내용을 나타내주는 컴포넌트입니다.',
      },
    },
  },
};
