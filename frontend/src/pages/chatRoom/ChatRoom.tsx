import React, { useEffect, useRef, useState } from 'react';

import styled from '@emotion/styled';
import { Client } from '@stomp/stompjs';
import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';
import SockJS from 'sockjs-client';

import { getMentoringDetail } from '../../common/apis/getMentoringDetail';

import { getChatRoom } from './apis/getChatRoom';
import { getChatRoomInfo } from './apis/getChatRoomInfo';
import ChatContent from './components/ChatContent/ChatContent';
import ChatRoomHeader from './components/ChatRoomHeader/ChatRoomHeader';
import InputSection from './components/InputSection/InputSection';
import MentoringActionPanel from './components/MentoringActionPanel/MentoringActionPanel';

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

  const stompClientRef = useRef<Client | null>(null);

  const { data: chantRoomMessage } = useQuery({
    queryKey: ['chatRoom', chatRoomId],
    queryFn: () => getChatRoom(Number(chatRoomId!)),
  });

  const ChatRoomInfoQuery = useQuery<ChatRoomInfo>({
    queryKey: ['chatRoomInfo', chatRoomId],
    queryFn: () => getChatRoomInfo(Number(chatRoomId!)),
  });

  const chantRoomInfo = ChatRoomInfoQuery.data;
  const mentoringId = chantRoomInfo?.mentoringId;

  const { data: mentoring, isPending } = useQuery({
    queryKey: ['mentoring', mentoringId],
    queryFn: () => getMentoringDetail(String(mentoringId)),
    enabled: !!mentoringId,
  });

  useEffect(() => {
    if (chantRoomMessage) {
      setMessages(chantRoomMessage);
    }
  }, [chantRoomMessage]);

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
      body: JSON.stringify({ content: message, chatRoomId, tempId }),
    });
  };

  return (
    <S_Container>
      {isPending && (
        // TODO: 추후 스켈레톤으로 변경
        <div>로딩중</div>
      )}
      {chantRoomInfo && mentoring && (
        <div>
          <ChatRoomHeader name={chantRoomInfo.opponentName} />
          <MentoringActionPanel
            mentorName={mentoring.mentorName}
            price={mentoring.price}
            profileImageUrl={mentoring.profileImageUrl}
            mentorOwned={true}
            onPaymentRequestClick={handlePaymentRequestClick}
            onReviewRequestClick={handleReviewRequestClick}
            onEndClick={handleEndClick}
            onPaymentClick={handlePaymentClick}
            onReviewClick={handleReviewClick}
          />
        </div>
      )}
      <ChatContent messages={messages} />
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
