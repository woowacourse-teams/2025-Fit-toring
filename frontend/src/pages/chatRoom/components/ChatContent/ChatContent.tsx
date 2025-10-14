import { useLayoutEffect, useRef } from 'react';

import styled from '@emotion/styled';

import ChatBubble from '../ChatBubble/ChatBubble';

import type { Message } from '../../types/message';

interface ChatContentProps {
  messages: Message[];
}

function ChatContent({ messages }: ChatContentProps) {
  const storedData = localStorage.getItem('memberId');
  const memberId = storedData ? JSON.parse(storedData) : null;

  const containerRef = useRef<HTMLDivElement>(null);

  useLayoutEffect(() => {
    const element = containerRef.current;
    if (!element) {
      return;
    }
    element.scrollTop = element.scrollHeight;
  }, [messages.length]);

  return (
    <S_Container ref={containerRef}>
      {messages.map(
        ({ content, createdAt, senderId, status, chatMessageId }, index) => {
          const prevSenderId = index > 0 ? messages[index - 1].senderId : null;
          const senderChanged = prevSenderId !== senderId;

          return (
            <S_ChatBubbleWrapper
              key={chatMessageId}
              senderChanged={senderChanged}
            >
              <ChatBubble
                content={content}
                createdAt={createdAt}
                authored={senderId === memberId}
                status={status}
              />
            </S_ChatBubbleWrapper>
          );
        },
      )}
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
