import { Fragment } from 'react';

import styled from '@emotion/styled';

import calendarIcon from '../../../../common/assets/images/calendarIcon.svg';
import {
  formatChatDateDivider,
  isSameLocalDate,
  isSameLocalMinute,
} from '../../../../common/utils/formatToKoreanTime';
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

const getLocalDateKey = (createdAt: string) => {
  const date = new Date(createdAt);

  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
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
          const prevMessage = index > 0 ? messages[index - 1] : null;
          const prevSenderId = prevMessage?.senderId ?? null;
          const nextMessage =
            index < messages.length - 1 ? messages[index + 1] : null;
          const senderChanged = prevSenderId !== message.senderId;
          const showDateDivider =
            !prevMessage ||
            !isSameLocalDate(prevMessage.createdAt, message.createdAt);
          const showTime =
            !nextMessage ||
            nextMessage.senderId !== message.senderId ||
            !isSameLocalMinute(message.createdAt, nextMessage.createdAt);

          if (message.messageType === 'IMAGE') {
            return (
              <Fragment key={getMessageKey(message)}>
                {showDateDivider ? (
                  <DateDivider createdAt={message.createdAt} />
                ) : null}
                <S_ChatBubbleWrapper senderChanged={senderChanged}>
                  <ChatImageBubble
                    content={message.thumbnailUrl ?? message.originalImageUrl}
                    createdAt={message.createdAt}
                    authored={message.senderId === Number(memberId)}
                    showTime={showTime}
                    status={message.status}
                  />
                </S_ChatBubbleWrapper>
              </Fragment>
            );
          }

          return (
            <Fragment key={getMessageKey(message)}>
              {showDateDivider ? (
                <DateDivider createdAt={message.createdAt} />
              ) : null}
              <S_ChatBubbleWrapper senderChanged={senderChanged}>
                <ChatBubble
                  content={message.content}
                  createdAt={message.createdAt}
                  authored={message.senderId === Number(memberId)}
                  showTime={showTime}
                  status={message.status}
                />
              </S_ChatBubbleWrapper>
            </Fragment>
          );
        })}
      </S_BubbleList>
    </S_Container>
  );
}

function DateDivider({ createdAt }: { createdAt: string }) {
  return (
    <S_DateDividerWrapper>
      <S_DateDivider data-date-key={getLocalDateKey(createdAt)}>
        <S_DateIcon src={calendarIcon} alt="" />
        <S_DateText>{formatChatDateDivider(createdAt)}</S_DateText>
      </S_DateDivider>
    </S_DateDividerWrapper>
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

const S_DateDividerWrapper = styled.div`
  display: flex;
  justify-content: center;

  margin: 1.6rem 0 0.8rem;
`;

const S_DateDivider = styled.div`
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;

  padding: 0.7rem 1.6rem;
  border-radius: 999px;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY50};
`;

const S_DateIcon = styled.img`
  width: 2rem;
  height: 2rem;
`;

const S_DateText = styled.span`
  color: ${({ theme }) => theme.SYSTEM.GRAY700};
  ${({ theme }) => theme.TYPOGRAPHY.C4_R};
`;

const S_ChatBubbleWrapper = styled.div<{ senderChanged: boolean }>`
  margin-top: ${({ senderChanged }) => (senderChanged ? '1.5rem' : '0.8rem')};
`;
