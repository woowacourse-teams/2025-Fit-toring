import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';

import { getChatRooms } from './apis/getChatRooms';
import ChatRoomList from './components/ChatRoomList/ChatRoomList';
import ChatRoomsHeader from './components/ChatRoomsHeader/ChatRoomsHeader';

export interface ChatRoomListItemType {
  chatRoomId: string;
  name: string;
  lastMessage: string;
  timeText: string;
  imageUrl?: string | null;
}

function ChatRooms() {
  const { data: chatRoomsData } = useQuery({
    queryKey: ['chatRooms'],
    queryFn: () => getChatRooms(),
  });

  return (
    <S_Container>
      <ChatRoomsHeader />
      <S_ChatRoomListSection>
        <ChatRoomList
          chatList={chatRoomsData ?? []}
          onChatRoomListClick={() => {}}
        />
      </S_ChatRoomListSection>
    </S_Container>
  );
}

export default ChatRooms;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
`;

const S_ChatRoomListSection = styled.div`
  flex-grow: 1;

  height: calc(100% - 5.7rem);
`;
