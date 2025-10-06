import styled from '@emotion/styled';

import ChatBubble from '../ChatBubble/ChatBubble';

type MessageType = {
  content: string;
  createdAt: string;
  senderId: string;
};

interface ChatContentProps {
  messages: MessageType[]; // 추후 타입 변경
}

function ChatContent({ messages }: ChatContentProps) {
  const myId = '1';

  return (
    <S_Container>
      {messages.map(({ content, createdAt, senderId }, index) => {
        const prevSenderId = index > 0 ? messages[index - 1].senderId : null;
        const senderChanged = prevSenderId !== senderId;

        return (
          <S_ChatBubbleWrapper key={index} senderChanged={senderChanged}>
            <ChatBubble
              content={content}
              createdAt={createdAt}
              authored={senderId === myId}
            />
          </S_ChatBubbleWrapper>
        );
      })}
    </S_Container>
  );
}

export default ChatContent;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  flex-grow: 1;

  padding: 1.6rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
  overflow-y: auto;
`;

const S_ChatBubbleWrapper = styled.div<{ senderChanged: boolean }>`
  margin-top: ${({ senderChanged }) => (senderChanged ? '1.5rem' : '0.8rem')};
`;
