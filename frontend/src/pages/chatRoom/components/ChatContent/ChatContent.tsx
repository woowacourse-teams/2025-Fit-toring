import { useEffect } from 'react';

import styled from '@emotion/styled';

import ChatBubble from '../ChatBubble/ChatBubble';

import type { Message } from '../../types/message';

interface ChatContentProps {
  messages: Message[];
  pageFirstElRef: React.RefObject<HTMLDivElement | null>;
  listElRef: React.RefObject<HTMLDivElement | null>;
}

function ChatContent({
  messages,
  pageFirstElRef,
  listElRef,
}: ChatContentProps) {
  const storedData = localStorage.getItem('memberId');
  const parsedData = storedData ? JSON.parse(storedData) : null;
  const memberId = parsedData ? parsedData.memberId : null;

  useEffect(() => {
    const element = listElRef.current;
    if (!element) {
      return;
    }

    const isAtBottom =
      element.scrollHeight - element.scrollTop - element.clientHeight < 50;

    if (isAtBottom) {
      element.scrollTop = element.scrollHeight;
    }
  }, [listElRef, messages.length]);

  if (!memberId) {
    return null; // TODO: 에러 UI로 변경할 예정
  }

  return (
    <S_Container ref={listElRef}>
      <div ref={pageFirstElRef} style={{ height: 1, flex: '0 0 1px' }} />

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
