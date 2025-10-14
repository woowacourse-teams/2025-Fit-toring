import { useEffect, useLayoutEffect, useRef } from 'react';

import styled from '@emotion/styled';

import ChatBubble from '../ChatBubble/ChatBubble';

import type { Message } from '../../types/message';

interface ChatContentProps {
  messages: Message[];
  pageFirstRef: React.RefObject<HTMLDivElement | null>;
  listRef: React.RefObject<HTMLDivElement | null>;
}

function ChatContent({ messages, pageFirstRef, listRef }: ChatContentProps) {
  const storedData = localStorage.getItem('memberId');
  const memberId = storedData ? JSON.parse(storedData) : null;

  const initialScrolledRef = useRef(false);

  useLayoutEffect(() => {
    const element = listRef.current;
    if (!element) {
      return;
    }

    if (!initialScrolledRef.current && messages.length > 0) {
      element.scrollTop = element.scrollHeight;
      initialScrolledRef.current = true;
    }
  }, [listRef, messages]);

  useEffect(() => {
    const element = listRef.current;
    if (!element) {
      return;
    }

    const isAtBottom =
      element.scrollHeight - element.scrollTop - element.clientHeight < 50;

    if (isAtBottom) {
      element.scrollTop = element.scrollHeight;
    }
  }, [listRef, messages.length]);

  return (
    <S_Container ref={listRef}>
      <div ref={pageFirstRef} />

      <S_BubbleList>
        {messages.map(
          ({ content, createdAt, senderId, status, chatMessageId }, index) => {
            const prevSenderId =
              index > 0 ? messages[index - 1].senderId : null;
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
      </S_BubbleList>
    </S_Container>
  );
}

export default ChatContent;

const S_Container = styled.div`
  background-color: ${({ theme }) => theme.BG.WHITE};

  overflow-y: auto;
`;

const S_BubbleList = styled.div`
  display: flex;
  flex-direction: column;
  flex-grow: 1;

  padding: 1.6rem;
`;

const S_ChatBubbleWrapper = styled.div<{ senderChanged: boolean }>`
  margin-top: ${({ senderChanged }) => (senderChanged ? '1.5rem' : '0.8rem')};
`;
