import styled from '@emotion/styled';

import ChatBubble from '../ChatBubble/ChatBubble';
import ChatImageBubble from '../ChatImageBubble/ChatImageBubble';

import type { Message } from '../../types/message';

interface ChatContentProps {
  messages: Message[];
  pageFirstElRef: React.RefObject<HTMLDivElement | null>;
  listElRef: React.RefObject<HTMLDivElement | null>;
}

const getMessageKey = (message: Message) => {
  return (
    message.chatMessageId ??
    message.messageId ??
    message.tempId ??
    `${message.senderId}-${message.createdAt}-${message.content ?? message.originalImageUrl}`
  );
};

function ChatContent({
  messages,
  pageFirstElRef,
  listElRef,
}: ChatContentProps) {
  const memberId = localStorage.getItem('memberId');

  if (!memberId) {
    return null; // TODO: 에러 UI로 변경할 예정
  }

  return (
    <S_Container ref={listElRef}>
      <div ref={pageFirstElRef} style={{ height: 1, flex: '0 0 1px' }} />

      <S_BubbleList>
        {messages.map((message, index) => {
          const prevSenderId = index > 0 ? messages[index - 1].senderId : null;
          const senderChanged = prevSenderId !== message.senderId;

          if (message.messageType === 'IMAGE') {
            return (
              <S_ChatBubbleWrapper
                key={getMessageKey(message)}
                senderChanged={senderChanged}
              >
                <ChatImageBubble
                  content={message.thumbnailUrl ?? message.originalImageUrl}
                  createdAt={message.createdAt}
                  authored={message.senderId === Number(memberId)}
                  status={message.status}
                />
              </S_ChatBubbleWrapper>
            );
          }

          return (
            <S_ChatBubbleWrapper
              key={getMessageKey(message)}
              senderChanged={senderChanged}
            >
              <ChatBubble
                content={message.content}
                createdAt={message.createdAt}
                authored={message.senderId === Number(memberId)}
                status={message.status}
              />
            </S_ChatBubbleWrapper>
          );
        })}
      </S_BubbleList>
    </S_Container>
  );
}

export default ChatContent;

const S_Container = styled.div`
  display: flex;
  flex: 1 1 auto;
  flex-direction: column;

  min-height: 0;
  padding: 1.6rem;

  background-color: ${({ theme }) => theme.BG.WHITE};

  overflow-y: auto;
`;

const S_BubbleList = styled.div``;

const S_ChatBubbleWrapper = styled.div<{ senderChanged: boolean }>`
  margin-top: ${({ senderChanged }) => (senderChanged ? '1.5rem' : '0.8rem')};
`;
