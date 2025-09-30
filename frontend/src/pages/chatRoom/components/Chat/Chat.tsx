import styled from '@emotion/styled';

import ChatBubble from '../ChatBubble/ChatBubble';

type MessageType = {
  text: string;
  createdAt: string;
  sendId: string;
};

interface ChatProps {
  messages: MessageType[]; // 추후 타입 변경
}

function Chat({ messages }: ChatProps) {
  const myId = '1';

  return (
    <S_Container>
      {messages.map(({ text, createdAt, sendId }) => {
        if (sendId === myId) {
          return (
            <ChatBubble text={text} createdAt={createdAt} authored={true} />
          );
        }

        return (
          <ChatBubble text={text} createdAt={createdAt} authored={false} />
        );
      })}
    </S_Container>
  );
}

export default Chat;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;

  padding: 1.6rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
