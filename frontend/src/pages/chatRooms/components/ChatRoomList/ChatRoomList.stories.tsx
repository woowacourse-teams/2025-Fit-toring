import ChatRoomList from './ChatRoomList';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'ChatRooms/ChatRoomList',
  component: ChatRoomList,
  decorators: [(Story) => <Story />],
} satisfies Meta<typeof ChatRoomList>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    chatList: [
      {
        chatRoomId: 'room-1',
        name: '김멘토',
        lastMessage: '안녕하세요! 멘토링 시작하겠습니다 🙂',
        timeText: '2025-09-27T16:35:03',
        imageUrl: null,
      },
      {
        chatRoomId: 'room-2',
        name: '이멘티',
        lastMessage:
          '네 안녕하세요! 혹시 오늘 몇 시쯤 어디에서 만나면 좋을까요? 장소도 같이 정하면 좋을 것 같아요.',
        timeText: '2025-09-27T16:05:03',
        imageUrl: 'https://picsum.photos/seed/chatroom2/80/80',
      },
      {
        chatRoomId: 'room-3',
        name: '박멘토',
        lastMessage:
          '좋아요. 그럼 목표부터 정리해볼까요? (1) 현재 상황 (2) 목표 (3) 기간 순으로 알려주시면 더 빠르게 도와드릴게요!',
        timeText: '2025-09-27T14:35:03',
        imageUrl: 'https://picsum.photos/seed/chatroom3/80/80',
      },
    ],
    onChatRoomListClick: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          'ChatRoomList 컴포넌트는 채팅방 목록을 렌더링합니다. 각 아이템은 프로필 이미지, 이름, 마지막 메시지(최대 2줄 말줄임), 시간을 표시하며, 아이템 클릭 시 onChatListClick(chatRoomId)가 호출됩니다.',
      },
    },
  },
};

export const Empty: Story = {
  args: {
    chatList: [],
    onChatRoomListClick: () => {},
  },
  parameters: {
    docs: {
      description: {
        story: '채팅방 목록이 비어있는 상태를 확인하는 스토리입니다.',
      },
    },
  },
};
