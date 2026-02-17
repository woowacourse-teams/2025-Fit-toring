import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import ApiError from '../../common/apis/ApiError';
import { PAGE_URL } from '../../common/constants/url';

import { getChatRooms } from './apis/getChatRooms';
import ChatRoomList from './components/ChatRoomList/ChatRoomList';
import ChatRoomsHeader from './components/ChatRoomsHeader/ChatRoomsHeader';

function ChatRooms() {
  const navigate = useNavigate();

  const { data: chatRoomsData } = useQuery({
    queryKey: ['chatRooms'],
    queryFn: () => getChatRooms(),
    retry: (failureCount, error) => {
      if (error instanceof ApiError && error.status === 401) {
        return false;
      }

      return failureCount < 1;
    },
  });

  const handleChatRoomListClick = (chatId: number) => {
    navigate(`${PAGE_URL.CHAT_ROOM}/${chatId}`);
  };

  return (
    <S_Container>
      <ChatRoomsHeader />
      <S_ChatRoomListSection>
        <ChatRoomList
          chatList={chatRoomsData ?? []}
          onChatRoomListClick={handleChatRoomListClick}
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
