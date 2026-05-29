import { createRef } from 'react';

import ChatContent from './ChatContent';

import type { Meta, StoryObj } from '@storybook/react-webpack5';
import type { Message } from '../../types/message';

const createTextMessage = (
  chatMessageId: number,
  content: string,
  createdAt: string,
  senderId: number,
): Message => ({
  chatMessageId,
  chatRoomId: 1,
  content,
  createdAt,
  messageType: 'TEXT',
  originalImageUrl: null,
  senderId,
  status: 'success',
  tempId: null,
  thumbnailUrl: null,
});

const meta = {
  title: 'ChatRoom/ChatContent',
  component: ChatContent,
  decorators: [
    (Story) => {
      localStorage.setItem('memberId', '1');
      return <Story />;
    },
  ],
} satisfies Meta<typeof ChatContent>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    messages: [
      createTextMessage(
        1,
        '안녕하세요 회원님 멘토링 시작하겠습니다! 저는 멘토 김멘토입니다.',
        '2026-05-27T23:59:03',
        2,
      ),
      createTextMessage(
        2,
        '넵 안녕하세요. 몇시쯤 어디서 만날까요?',
        '2026-05-28T02:40:03',
        1,
      ),
      createTextMessage(
        3,
        '오전 10시에 만나는 건 어떠세요?',
        '2026-05-28T02:40:30',
        1,
      ),
      createTextMessage(
        4,
        '좋아요. 장소도 같이 정해볼까요?',
        '2026-05-28T02:41:03',
        2,
      ),
    ],
    pageFirstElRef: createRef<HTMLDivElement>(),
    listElRef: createRef<HTMLDivElement>(),
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
