import { createRef } from 'react';

import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import ChatContent from '../src/pages/chatRoom/components/ChatContent/ChatContent';

import { render, screen } from './utils';

import type { Message, TextMessage } from '../src/pages/chatRoom/types/message';

let chatMessageId = 0;

const renderChatContent = (messages: Message[]) => {
  return render(
    <ChatContent
      messages={messages}
      pageFirstElRef={createRef<HTMLDivElement>()}
      listElRef={createRef<HTMLDivElement>()}
    />,
  );
};

type TextMessageOverrides = Partial<Omit<TextMessage, 'content' | 'createdAt'>> &
  Pick<TextMessage, 'content' | 'createdAt'>;

const createTextMessage = ({
  content,
  createdAt,
  ...overrides
}: TextMessageOverrides): TextMessage => ({
  chatMessageId: ++chatMessageId,
  chatRoomId: 1,
  content,
  createdAt,
  messageType: 'TEXT',
  originalImageUrl: null,
  senderId: 1,
  status: 'success',
  tempId: null,
  thumbnailUrl: null,
  ...overrides,
});

describe('ChatContent 같은 분 시간 표시', () => {
  beforeEach(() => {
    chatMessageId = 0;
    localStorage.setItem('memberId', '1');
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('같은 발신자가 같은 분에 연속으로 보낸 메시지는 마지막 메시지에만 시간을 표시한다.', () => {
    renderChatContent([
      createTextMessage({
        content: '첫 번째 메시지',
        createdAt: '2026-05-28T14:40:00',
        senderId: 1,
      }),
      createTextMessage({
        content: '두 번째 메시지',
        createdAt: '2026-05-28T14:40:30',
        senderId: 1,
      }),
    ]);

    expect(screen.getByText('첫 번째 메시지')).toBeInTheDocument();
    expect(screen.getByText('두 번째 메시지')).toBeInTheDocument();
    expect(screen.getAllByText('오후 2:40')).toHaveLength(1);
  });

  it('발신자가 바뀌면 같은 분이어도 각 그룹의 마지막 메시지에 시간을 표시한다.', () => {
    renderChatContent([
      createTextMessage({
        content: '내 메시지',
        createdAt: '2026-05-28T14:40:00',
        senderId: 1,
      }),
      createTextMessage({
        content: '상대 메시지',
        createdAt: '2026-05-28T14:40:30',
        senderId: 2,
      }),
    ]);

    expect(screen.getAllByText('오후 2:40')).toHaveLength(2);
  });

  it('같은 발신자여도 분이 다르면 각각 시간을 표시한다.', () => {
    renderChatContent([
      createTextMessage({
        content: '2시 40분 메시지',
        createdAt: '2026-05-28T14:40:59',
        senderId: 1,
      }),
      createTextMessage({
        content: '2시 41분 메시지',
        createdAt: '2026-05-28T14:41:00',
        senderId: 1,
      }),
    ]);

    expect(screen.getByText('오후 2:40')).toBeInTheDocument();
    expect(screen.getByText('오후 2:41')).toBeInTheDocument();
  });

  it('시간이 숨겨져도 실패 표시는 유지한다.', () => {
    renderChatContent([
      createTextMessage({
        content: '실패 메시지',
        createdAt: '2026-05-28T14:40:00',
        senderId: 1,
        status: 'fail',
      }),
      createTextMessage({
        content: '다음 메시지',
        createdAt: '2026-05-28T14:40:30',
        senderId: 1,
      }),
    ]);

    expect(screen.getByText('전송실패')).toBeInTheDocument();
    expect(screen.getAllByText('오후 2:40')).toHaveLength(1);
  });
});

describe('ChatContent 날짜 구분선', () => {
  beforeEach(() => {
    chatMessageId = 0;
    localStorage.setItem('memberId', '1');
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('첫 메시지 앞에 날짜 구분선을 표시한다.', () => {
    renderChatContent([
      createTextMessage({
        content: '첫 메시지',
        createdAt: '2026-05-28T14:40:00',
      }),
    ]);

    expect(screen.getByText('2026년 5월 28일 목요일')).toBeInTheDocument();
  });

  it('같은 날짜 메시지에는 날짜 구분선을 반복하지 않는다.', () => {
    renderChatContent([
      createTextMessage({
        content: '첫 메시지',
        createdAt: '2026-05-28T14:40:00',
      }),
      createTextMessage({
        content: '두 번째 메시지',
        createdAt: '2026-05-28T15:40:00',
      }),
    ]);

    expect(screen.getAllByText('2026년 5월 28일 목요일')).toHaveLength(1);
  });

  it('날짜가 바뀌면 새 날짜 구분선을 표시한다.', () => {
    renderChatContent([
      createTextMessage({
        content: '이전 날짜 메시지',
        createdAt: '2026-05-27T23:59:00',
      }),
      createTextMessage({
        content: '다음 날짜 메시지',
        createdAt: '2026-05-28T00:00:00',
      }),
    ]);

    expect(screen.getByText('2026년 5월 27일 수요일')).toBeInTheDocument();
    expect(screen.getByText('2026년 5월 28일 목요일')).toBeInTheDocument();
  });
});
