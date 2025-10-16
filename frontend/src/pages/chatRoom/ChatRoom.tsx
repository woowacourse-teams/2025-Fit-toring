import React, { useEffect, useLayoutEffect, useRef, useState } from 'react';

import styled from '@emotion/styled';
import { Client } from '@stomp/stompjs';
import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';
import SockJS from 'sockjs-client';

import { getChatRoomInfo } from './apis/getChatRoomInfo';
import ChatContent from './components/ChatContent/ChatContent';
import ChatRoomHeader from './components/ChatRoomHeader/ChatRoomHeader';
import InputSection from './components/InputSection/InputSection';
import MentoringActionPanel from './components/MentoringActionPanel/MentoringActionPanel';
import useInfiniteChatRoomMessage from './hooks/useInfiniteChatRoomMessage';

import type { ChatRoomInfo } from './types/chatRoomInfo';
import type { Message } from './types/message';
import type { IMessage } from '@stomp/stompjs';

function ChatRoom() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [message, setMessage] = useState('');

  const { chatRoomId } = useParams();

  const storedData = localStorage.getItem('memberId');
  const memberId = storedData ? JSON.parse(storedData) : null;

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setMessage(e.target.value);
  };

  const handlePaymentRequestClick = (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>,
  ) => {};

  const handleReviewRequestClick = (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>,
  ) => {};

  const handleEndClick = (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>,
  ) => {};

  const handlePaymentClick = (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>,
  ) => {};

  const handleReviewClick = (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>,
  ) => {};

  const { chatRoomMessage, fetchNextPage, isFetchingNextPage, hasNextPage } =
    useInfiniteChatRoomMessage(Number(chatRoomId!));

  const pageFirstRef = useRef<HTMLDivElement | null>(null);
  const listRef = useRef<HTMLDivElement>(null);

  const initialScrolledRef = useRef(false);
  const ioReadyRef = useRef(false);
  const expectPrependRef = useRef<null | { prevH: number; prevTop: number }>(
    null,
  );

  useLayoutEffect(() => {
    const element = listRef.current;
    if (!element) {
      return;
    }

    if (!initialScrolledRef.current && messages.length > 0) {
      element.scrollTop = element.scrollHeight;
      initialScrolledRef.current = true;
      requestAnimationFrame(() => {
        ioReadyRef.current = true;
      });
    }
  }, [listRef, messages]);

  const stateRef = useRef({
    hasNextPage: false,
    isFetchingNextPage: false,
  });

  useEffect(() => {
    stateRef.current.hasNextPage = !!hasNextPage;
    stateRef.current.isFetchingNextPage = !!isFetchingNextPage;
  }, [hasNextPage, isFetchingNextPage]);

  useEffect(() => {
    const target = pageFirstRef.current;
    const list = listRef.current;

    if (!target || !list) {
      return;
    }

    const observer = new IntersectionObserver(
      async (entries) => {
        const { hasNextPage, isFetchingNextPage } = stateRef.current;
        if (
          !entries[0].isIntersecting ||
          !ioReadyRef.current ||
          !hasNextPage ||
          isFetchingNextPage
        ) {
          return;
        }

        expectPrependRef.current = {
          prevH: list.scrollHeight,
          prevTop: list.scrollTop,
        };
        await fetchNextPage();
      },
      {
        root: listRef.current,
        threshold: 0.1,
        rootMargin: '20px 0px 0px 0px',
      },
    );

    observer.observe(target);

    return () => observer.disconnect();
  }, [fetchNextPage, pageFirstRef.current, listRef.current]);

  const topKey = messages[0]?.chatMessageId;

  useLayoutEffect(() => {
    const list = listRef.current;
    const snap = expectPrependRef.current;
    if (!list || !snap) {
      return;
    }

    const delta = list.scrollHeight - snap.prevH;
    list.scrollTop = snap.prevTop + delta;

    expectPrependRef.current = null;
  }, [topKey]);

  useEffect(() => {
    if (chatRoomMessage) {
      setMessages(chatRoomMessage.pages.flatMap((page) => page.chatMessages));
    }
  }, [chatRoomMessage]);

  const ChatRoomInfoQuery = useQuery<ChatRoomInfo>({
    queryKey: ['chatRoomInfo', chatRoomId],
    queryFn: () => getChatRoomInfo(Number(chatRoomId!)),
  });

  const chatRoomInfo = ChatRoomInfoQuery.data;

  const stompClientRef = useRef<Client | null>(null);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () =>
        new SockJS(`${process.env.API_BASE_URL}/ws-chat`, null, {
          withCredentials: true,
        }),
      onStompError: (frame) => console.error('STOMP protocol error:', frame),
      onWebSocketError: (event) => console.error('WebSocket error:', event),
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe(
          `/topic/chatroom/${chatRoomId}`,
          (message: IMessage) => {
            const parsedMessage = JSON.parse(message.body);

            setMessages((prev) => {
              if (parsedMessage.tempId) {
                const index = prev.findIndex(
                  (m) => Number(m.tempId) === Number(parsedMessage.tempId),
                );
                if (index !== -1) {
                  const newArr = [...prev];
                  newArr[index] = {
                    ...parsedMessage,
                    status: 'success',
                  };
                  return newArr;
                }
              }

              const exists = prev.some(
                (m) =>
                  m.chatMessageId &&
                  m.chatMessageId === parsedMessage.chatMessageId,
              );
              if (exists) {
                return prev;
              }

              return [...prev, { ...parsedMessage, status: 'success' }];
            });
          },
        );
      },
    });

    stompClientRef.current = client;
    client.activate();

    return () => {
      client.deactivate();
    };
  }, [chatRoomId]);

  const handleMessageSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const tempId = Date.now();

    const optimisticMsg = {
      senderId: memberId,
      content: message,
      createdAt: new Date().toString(),
      chatRoomId: Number(chatRoomId),
      chatMessageId: tempId,
      tempId,
      status: 'pending' as const,
    };

    setMessages((prev) => [...prev, optimisticMsg]);
    setMessage('');

    const client = stompClientRef.current;
    if (!client || !client.connected || memberId === null) {
      return;
    }

    client.publish({
      destination: `/app/chatroom/${chatRoomId}`,
      body: JSON.stringify({ content: message, tempId }),
    });
  };

  if (ChatRoomInfoQuery.isPending) {
    return (
      <S_Container>
        <div>로딩중</div>
      </S_Container>
    );
  }

  if (!chatRoomInfo) {
    return null;
  }

  const { chatRoomInfoDto, mentoringInfoDto } = chatRoomInfo;

  return (
    <S_Container>
      <div>
        <ChatRoomHeader name={chatRoomInfoDto.opponentName} />
        <MentoringActionPanel
          mentorName={mentoringInfoDto.mentorName}
          price={mentoringInfoDto.price}
          profileImageUrl={mentoringInfoDto.profileImageUrl}
          mentorOwned={chatRoomInfoDto.myRole === 'MENTOR'}
          onPaymentRequestClick={handlePaymentRequestClick}
          onReviewRequestClick={handleReviewRequestClick}
          onEndClick={handleEndClick}
          onPaymentClick={handlePaymentClick}
          onReviewClick={handleReviewClick}
        />
      </div>

      <ChatContent
        messages={messages}
        pageFirstRef={pageFirstRef}
        listRef={listRef}
      />
      <InputSection
        value={message}
        onChange={handleChange}
        onSubmit={handleMessageSubmit}
      />
    </S_Container>
  );
}

export default ChatRoom;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;

  height: 100svh;
`;
