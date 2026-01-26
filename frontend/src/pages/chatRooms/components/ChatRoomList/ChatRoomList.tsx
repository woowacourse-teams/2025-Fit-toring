import styled from '@emotion/styled';

import ChatRoomListItem from '../ChatRoomListItem/ChatRoomListItem';

import type { ChatRoomListItemType } from '../../ChatRooms';

interface ChatRoomListProps {
  chatList: ChatRoomListItemType[];
  onChatRoomListClick: (chatId: string) => void;
}

function ChatRoomList({ chatList, onChatRoomListClick }: ChatRoomListProps) {
  if (chatList.length === 0) {
    return (
      <S_EmptyContainer>
        <S_EmptyText>아직 생성된 채팅방이 없습니다.</S_EmptyText>
        <S_EmptySubText>
          멘토링이 시작되면 이곳에서 채팅을 주고받을 수 있어요.
        </S_EmptySubText>
      </S_EmptyContainer>
    );
  }

  return (
    <S_List>
      {chatList.map((chat) => (
        <ChatRoomListItem
          key={chat.chatRoomId}
          chat={chat}
          onClick={onChatRoomListClick}
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

const S_EmptyContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  height: 100%;
  padding: 4rem 2rem;

  text-align: center;
`;

const S_EmptyText = styled.p`
  margin: 0;

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_B};
`;

const S_EmptySubText = styled.p`
  margin-top: 0.8rem;

  color: ${({ theme }) => theme.SYSTEM.GRAY600};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
`;
