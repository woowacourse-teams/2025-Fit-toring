import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import ApiError from '../../common/apis/ApiError';
import PullToRefresh from '../../common/components/PullToRefresh/PullToRefresh';
import { isPullToRefreshEnabled } from '../../common/components/PullToRefresh/utils';
import { PAGE_URL } from '../../common/constants/url';

import { getChatRooms } from './apis/getChatRooms';
import ChatRoomList from './components/ChatRoomList/ChatRoomList';
import ChatRoomsHeader from './components/ChatRoomsHeader/ChatRoomsHeader';

function ChatRooms() {
  const navigate = useNavigate();

  const { data: chatRoomsData, refetch: refetchChatRooms } = useQuery({
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

  const handleRefresh = async () => {
    await refetchChatRooms();
  };

  return (
    <S_Container>
      <ChatRoomsHeader />
      <PullToRefresh
        enabled={isPullToRefreshEnabled()}
        onRefresh={handleRefresh}
      >
        <S_ChatRoomListSection>
          <ChatRoomList
            chatList={chatRoomsData ?? []}
            onChatRoomListClick={handleChatRoomListClick}
          />
        </S_ChatRoomListSection>
      </PullToRefresh>
    </S_Container>
  );
}

export default ChatRooms;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;

  height: 100%;
`;

const S_ChatRoomListSection = styled.div`
  flex-grow: 1;

  height: calc(100% - 5.7rem);
`;
