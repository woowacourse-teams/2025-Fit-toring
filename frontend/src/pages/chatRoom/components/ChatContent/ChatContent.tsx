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
      {messages.map(({ content, createdAt, senderId }) => {
        if (senderId === myId) {
          return (
            <ChatBubble
              content={content}
              createdAt={createdAt}
              authored={true}
            />
          );
        }

        return (
          <ChatBubble
            content={content}
            createdAt={createdAt}
            authored={false}
          />
        );
      })}
    </S_Container>
  );
}

export default ChatContent;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;

  padding: 1.6rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
