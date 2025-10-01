import React, { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import ChatContent from './components/ChatContent/ChatContent';
import ChatRoomHeader from './components/ChatRoomHeader/ChatRoomHeader';
import InputSection from './components/InputSection/InputSection';
import MentoringActionPanel from './components/MentoringActionPanel/MentoringActionPanel';

import type { Message } from './types/message';
import type SockJS from 'sockjs-client';
import { useQuery } from '@tanstack/react-query';
import { getChatRooms } from './apis/getChatRooms';
import { useParams } from 'react-router-dom';
import { captureSentryError } from '../../common/utils/captureSentryError';

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

  const [socket, setSocket] = useState<SockJS | null>(null);

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
    const ws = new WebSocket('wss://ws.postman-echo.com/raw');

    ws.onopen = () => console.log('✅ Connected');
    ws.onmessage = (event) => {
      const msg = JSON.parse(event.data);
      setMessages((prev) => [...prev, { ...msg, senderId: 2 }]);
    };
    ws.onclose = () => console.log('❌ Disconnected');

    setSocket(ws);

    return () => ws.close();
  }, []);

  const handleMessageSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (socket && socket.readyState !== WebSocket.OPEN) {
      return;
    }

    const tempId = Date.now();

    const optimisticMsg = {
      senderId: 1,
      content: message,
      createdAt: new Date().toString(),
      chatRoomId: Number(chatRoomId),
      id: tempId,
    };

    setMessages((prev) => [...prev, optimisticMsg]);
    setMessage('');

    try {
      socket.send(JSON.stringify(optimisticMsg));

      // POST요청 후 받은 응답값
      const savedMessage = {
        senderId: 1,
        content: message,
        createdAt: new Date().toString(),
        chatRoomId: Number(chatRoomId),
        id: tempId,
      };

      setMessages((prev) =>
        prev.map((message) => (message.id === tempId ? savedMessage : message)),
      );
    } catch (error) {
      setMessages((prev) =>
        prev.map((message) =>
          message.id === tempId ? { ...message, errored: true } : message,
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
