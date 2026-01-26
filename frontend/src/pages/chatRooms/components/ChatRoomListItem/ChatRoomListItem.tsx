import styled from '@emotion/styled';

import { formatToKoreanTime } from '../../../../common/utils/formatToKoreanTime';

import type { ChatRoomListItemType } from '../../ChatRooms';

interface ChatRoomListItemProps {
  chat: ChatRoomListItemType;
  onClick: (chatId: string) => void;
}

function ChatRoomListItem({ chat, onClick }: ChatRoomListItemProps) {
  return (
    <S_Container onClick={() => onClick(chat.chatRoomId)}>
      <S_Avatar>
        {chat.imageUrl ? (
          <S_AvatarImg src={chat.imageUrl} alt="프로필 사진" />
        ) : (
          <S_AvatarPlaceholder />
        )}
      </S_Avatar>

      <S_Middle>
        <S_Name>{chat.name}</S_Name>
        <S_Message>{chat.lastMessage}</S_Message>
      </S_Middle>

      <S_Time>{formatToKoreanTime(chat.timeText)}</S_Time>
    </S_Container>
  );
}

export default ChatRoomListItem;

const S_Container = styled.li`
  display: grid;
  grid-template-columns: 5.6rem 1fr auto;

  align-items: center;
  column-gap: 1.2rem;

  width: 100%;
  padding: 1.8rem 1.6rem;

  background: transparent;
  cursor: pointer;
`;

const S_Avatar = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  width: 5.6rem;
  height: 5.6rem;
  border-radius: 50%;

  background: #f2f2f2;
`;

const S_AvatarImg = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

const S_AvatarPlaceholder = styled.div`
  width: 100%;
  height: 100%;
  border-radius: 50%;

  background: #e9e9e9;
`;

const S_Middle = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.8rem;

  min-width: 0;
`;

const S_Name = styled.span`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_B};
`;

const S_Time = styled.span`
  color: ${({ theme }) => theme.SYSTEM.GRAY600};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
  white-space: nowrap;
`;

const S_Message = styled.p`
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;

  overflow: hidden;

  margin: 0;

  color: ${({ theme }) => theme.SYSTEM.GRAY600};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};

  overflow-wrap: break-word;
`;
