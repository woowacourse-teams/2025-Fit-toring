import React, { useEffect, useRef, useState } from 'react';

import styled from '@emotion/styled';
import { Client } from '@stomp/stompjs';
import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';
import SockJS from 'sockjs-client';

import { captureSentryError } from '../../common/utils/captureSentryError';

import { getChatRooms } from './apis/getChatRooms';
import ChatContent from './components/ChatContent/ChatContent';
import ChatRoomHeader from './components/ChatRoomHeader/ChatRoomHeader';
import InputSection from './components/InputSection/InputSection';
import MentoringActionPanel from './components/MentoringActionPanel/MentoringActionPanel';

import type { Message } from './types/message';
import type { IMessage } from '@stomp/stompjs';

const DUMMY_MESSAGES = [
  {
    content:
      '안녕하세요 회원님 멘토링 시작하겠습니다! 저는 멘토 김멘토입니다 어쩌구 저쩌구',
    createdAt: '2025-09-27T14:35:03',
    senderId: '1',
  },
  {
    content: '어떤 내용이 궁금하실까요?',
    createdAt: '2025-09-27T14:36:03',
    senderId: '1',
  },
  {
    content: '넵 안녕하세요 몇시쯤 어디서 만날까요?',
    createdAt: '2025-09-27T14:37:03',
    senderId: '2',
  },
  {
    content:
      '안녕하세요 회원님 멘토링 시작하겠습니다! 저는 멘토 김멘토입니다 어쩌구 저쩌구',
    createdAt: '2025-09-27T16:35:03',
    senderId: '1',
  },
  {
    content: '어떤 내용이 궁금하실까요?',
    createdAt: '2025-09-27T16:05:03',
    senderId: '1',
  },
  {
    content: '넵 안녕하세요 몇시쯤 어디서 만날까요?',
    createdAt: '2025-09-27T17:10:03',
    senderId: '2',
  },
  {
    content:
      '안녕하세요 회원님 멘토링 시작하겠습니다! 저는 멘토 김멘토입니다 어쩌구 저쩌구',
    createdAt: '2025-09-27T17:22:03',
    senderId: '1',
  },
  {
    content: '어떤 내용이 궁금하실까요?',
    createdAt: '2025-09-27T17:38:03',
    senderId: '1',
  },
  {
    content: '넵 안녕하세요 몇시쯤 어디서 만날까요?',
    createdAt: '2025-09-27T17:40:03',
    senderId: '2',
  },
  {
    content:
      '안녕하세요 회원님 멘토링 시작하겠습니다! 저는 멘토 김멘토입니다 어쩌구 저쩌구',
    createdAt: '2025-09-27T17:42:03',
    senderId: '1',
  },
  {
    content: '어떤 내용이 궁금하실까요?',
    createdAt: '2025-09-27T17:45:03',
    senderId: '1',
  },
  {
    content: '넵 안녕하세요 몇시쯤 어디서 만날까요?',
    createdAt: '2025-09-27T17:47:03',
    senderId: '2',
  },
  {
    content:
      '안녕하세요 회원님 멘토링 시작하겠습니다! 저는 멘토 김멘토입니다 어쩌구 저쩌구',
    createdAt: '2025-09-27T17:55:03',
    senderId: '1',
  },
  {
    content: '어떤 내용이 궁금하실까요?',
    createdAt: '2025-09-28T10:35:03',
    senderId: '1',
  },
  {
    content: '넵 안녕하세요 몇시쯤 어디서 만날까요?',
    createdAt: '2025-09-28T14:07:03',
    senderId: '2',
  },
  {
    content:
      '안녕하세요 회원님 멘토링 시작하겠습니다! 저는 멘토 김멘토입니다 어쩌구 저쩌구',
    createdAt: '2025-09-28T14:01:03',
    senderId: '1',
  },
  {
    content: '어떤 내용이 궁금하실까요?',
    createdAt: '2025-09-28T14:35:03',
    senderId: '1',
  },
  {
    content: '넵 안녕하세요 몇시쯤 어디서 만날까요?',
    createdAt: '2025-09-28T21:35:03',
    senderId: '2',
  },
];

function ChatRoom() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [message, setMessage] = useState('');

  const { chatRoomId } = useParams();

  const storedData = localStorage.getItem('memberId');
  const memberId = storedData ? JSON.parse(storedData).memberId : null;

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

  // const [socket, setSocket] = useState<SockJS | null>(null);
  const stompClientRef = useRef<Client | null>(null);

  const { data, isError, error } = useQuery({
    queryKey: ['chatrooms', chatRoomId],
    queryFn: () => getChatRooms(Number(chatRoomId!)),
  });

  useEffect(() => {
    if (data) {
      setMessages(data);
    }
  }, [data]);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () =>
        new SockJS(`http://${window.location.hostname}:8080/ws-chat`, null, {
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

            setMessages((prev) =>
              prev.map((m) =>
                m.tempId && m.tempId === parsedMessage.tempId
                  ? parsedMessage
                  : m,
              ),
            );
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

    const client = stompClientRef.current;
    if (!client || !client.connected || memberId === null) {
      return;
    }

    const tempId = Date.now();

    const optimisticMsg = {
      senderId: memberId,
      content: message,
      createdAt: new Date().toString(),
      chatRoomId: Number(chatRoomId),
      chatMessageId: tempId,
      tempId,
    };

    setMessages((prev) => [...prev, optimisticMsg]);
    setMessage('');

    try {
      client.publish({
        destination: `/add/chatroom/${chatRoomId}`,
        body: JSON.stringify({ content: message, chatRoomId, tempId }),
      });
    } catch (error) {
      setMessages((prev) =>
        prev.map((message) =>
          message.chatMessageId === tempId
            ? { ...message, status: 'fail' }
            : message,
        ),
      );

      captureSentryError({
        error,
        level: 'warning',
        feature: 'chat',
        step: 'chat-send',
      });
    }
  };

  return (
    <S_Container>
      <div>
        <ChatRoomHeader name="김멘토" />
        <MentoringActionPanel
          mentorName="김멘토"
          price={5000}
          profileImageUrl="https://techcourse-project-2025.s3.amazonaws.com/fit-toring/profile-image/default/94a63bf8-4e70-40e2-a3fe-de2d7c7724c5.jpg"
          mentorOwned={true}
          onPaymentRequestClick={handlePaymentRequestClick}
          onReviewRequestClick={handleReviewRequestClick}
          onEndClick={handleEndClick}
          onPaymentClick={handlePaymentClick}
          onReviewClick={handleReviewClick}
        />
      </div>

      <ChatContent messages={DUMMY_MESSAGES} />
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
