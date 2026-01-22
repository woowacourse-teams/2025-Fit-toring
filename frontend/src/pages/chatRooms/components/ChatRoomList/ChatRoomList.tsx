import styled from '@emotion/styled';

import ChatRoomListItem from '../ChatRoomListItem/ChatRoomListItem';

import type { ChatRoomListItemType } from '../../ChatRooms';

interface ChatRoomListProps {
  chatList: ChatRoomListItemType[];
  onChatListClick: (chatId: string) => void;
}

function ChatRoomList({ chatList, onChatListClick }: ChatRoomListProps) {
  return (
    <S_List>
      {chatList.map((chat) => (
        <ChatRoomListItem
          key={chat.chatRoomId}
          chat={chat}
          onClick={onChatListClick}
        />
      ))}
    </S_List>
  );
}

export default ChatRoomList;

const S_List = styled.ul`
  margin: 0;
  padding: 0;
  list-style: none;
`;
