import React, {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from 'react';

import styled from '@emotion/styled';
import { Client } from '@stomp/stompjs';
import { useQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';
import SockJS from 'sockjs-client';

import ApiError from '../../common/apis/ApiError';
import { postReissue } from '../../common/apis/postReissue';
import { PAGE_URL } from '../../common/constants/url';

import { getChatRoomInfo } from './apis/getChatRoomInfo';
import ChatContent from './components/ChatContent/ChatContent';
import ChatRoomForbidden from './components/ChatRoomForbidden/ChatRoomForbidden';
import ChatRoomHeader from './components/ChatRoomHeader/ChatRoomHeader';
import InputSection from './components/InputSection/InputSection';
import MentoringActionPanel from './components/MentoringActionPanel/MentoringActionPanel';
import { MESSAGE_TYPE } from './constants/message';
import useInfiniteChatRoomMessage from './hooks/useInfiniteChatRoomMessage';
import useScrollToBottomOnMessageSend from './hooks/useScrollToBottomOnMessageSend';
import useUpwardInfiniteScroll from './hooks/useUpwardInfiniteScroll';

import type { ChatRoomInfo } from './types/chatRoomInfo';
import type { Message } from './types/message';
import type { IMessage } from '@stomp/stompjs';

function ChatRoom() {
  const navigate = useNavigate();

  const [messages, setMessages] = useState<Message[]>([]);
  const messagesRef = useRef<Message[]>([]);
  const [message, setMessage] = useState('');

  const { chatRoomId } = useParams();

  const memberId = localStorage.getItem('memberId');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;

    if (value.length > 20_000) {
      return;
    }

    setMessage(value);
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

  const {
    data: chatRoomMessage,
    fetchNextPage,
    isFetchingNextPage,
    hasNextPage,
  } = useInfiniteChatRoomMessage(Number(chatRoomId!));

  const listElRef = useRef<HTMLDivElement | null>(null);
  const initialScrolledRef = useRef(false);
  const ioReadyRef = useRef(false);

  const stateRef = useRef({
    hasNextPage: false,
    isFetchingNextPage: false,
  });

  useEffect(() => {
    messagesRef.current = messages;
  }, [messages]);

  useEffect(() => {
    stateRef.current.hasNextPage = !!hasNextPage;
    stateRef.current.isFetchingNextPage = !!isFetchingNextPage;
  }, [hasNextPage, isFetchingNextPage]);

  const anchorKey = messages[0]?.chatMessageId;

  const shouldTrigger = useCallback(
    () =>
      ioReadyRef.current &&
      stateRef.current.hasNextPage &&
      !stateRef.current.isFetchingNextPage,
    [],
  );

  const onIntersect = useCallback(async () => {
    await fetchNextPage();
  }, [fetchNextPage]);

  const { pageFirstElRef } = useUpwardInfiniteScroll({
    shouldTrigger: shouldTrigger,
    onIntersect,
    anchorKey: anchorKey!,
    listElRef,
  });

  useLayoutEffect(() => {
    const element = listElRef.current;
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
  }, [listElRef, messages]);

  const { capturePrevScroll } = useScrollToBottomOnMessageSend({
    messageCount: messages.length,
    listElRef,
  });

  const handleMessageSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (message === '') {
      return;
    }

    capturePrevScroll();

    const tempId = Date.now();

    const optimisticMsg = {
      senderId: Number(memberId),
      content: message,
      createdAt: new Date().toString(),
      chatRoomId: Number(chatRoomId),
      chatMessageId: tempId,
      tempId,
      status: 'pending' as const,
      messageType: MESSAGE_TYPE.TEXT,
    };

    setMessages((prev) => [...prev, optimisticMsg]);
    setMessage('');

    const client = stompClientRef.current;
    if (!client || !client.connected || memberId === null) {
      return;
    }

    client.publish({
      destination: `/app/chatroom/${chatRoomId}`,
      body: JSON.stringify({ content: message, tempId, messageType: 'TEXT' }),
    });
  };

  useEffect(() => {
    if (chatRoomMessage) {
      setMessages(chatRoomMessage.pages.flatMap((page) => page.chatMessages));
    }
  }, [chatRoomMessage]);

  const {
    data: chatRoomInfoData,
    isPending: chatRoomInfoIsPending,
    error,
  } = useQuery<ChatRoomInfo, ApiError>({
    queryKey: ['chatRoomInfo', chatRoomId],
    queryFn: () => getChatRoomInfo(Number(chatRoomId!)),
    retry: (failureCount, error) => {
      if (error instanceof ApiError && error.status === 403) {
        return false;
      }

      return failureCount < 1;
    },
  });

  const stompClientRef = useRef<Client | null>(null);

  useEffect(() => {
    let isRefreshing = false;

    const client = new Client({
      webSocketFactory: () => {
        console.log('[sockjs] webSocketFactory called');

        return new SockJS(`${process.env.API_BASE_URL}/ws-chat`, null, {
          withCredentials: true,
        });
      },
      onStompError: async (frame) => {
        const parsedBody = JSON.parse(frame.body);

        if (parsedBody.code === 'TOKEN_EXPIRED') {
          if (isRefreshing) {
            return;
          }

          isRefreshing = true;

          try {
            await postReissue();

            if (client.active) {
              await client.deactivate();
            }

            client.activate();
          } catch (e) {
            navigate(PAGE_URL.LOGIN);
            console.error('토큰 재발급 실패:', e);
          } finally {
            isRefreshing = false;
          }
        }
      },

      onWebSocketError: (e) => console.error('WebSocket error:', e),
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe(
          `/topic/chatroom/${chatRoomId}`,
          (message: IMessage) => {
            capturePrevScroll();

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

        const pendingMessages = messagesRef.current.filter(
          (m) => m.status === 'pending',
        );

        pendingMessages.forEach((msg) => {
          client.publish({
            destination: `/app/chatroom/${chatRoomId}`,
            body: JSON.stringify({
              content: msg.content,
              tempId: msg.tempId,
              messageType: msg.messageType,
            }),
          });
        });
      },
    });

    stompClientRef.current = client;
    client.activate();

    return () => {
      client.deactivate();
    };
  }, [capturePrevScroll, chatRoomId, navigate]);

  if (error?.status === 403) {
    return <ChatRoomForbidden />;
  }

  if (error?.status === 401) {
    return <div>로그인 후 이용 가능합니다.</div>;
  }

  return (
    <S_Container>
      {chatRoomInfoIsPending || !chatRoomInfoData ? (
        <div>로딩중</div>
      ) : (
        <div>
          <ChatRoomHeader name={chatRoomInfoData.opponentName} />
          <MentoringActionPanel
            mentorName={chatRoomInfoData.mentorName}
            price={chatRoomInfoData.price}
            profileImageUrl={chatRoomInfoData.profileImageUrl}
            mentorOwned={chatRoomInfoData.myRole === 'MENTOR'}
            onPaymentRequestClick={handlePaymentRequestClick}
            onReviewRequestClick={handleReviewRequestClick}
            onEndClick={handleEndClick}
            onPaymentClick={handlePaymentClick}
            onReviewClick={handleReviewClick}
          />
        </div>
      )}

      <ChatContent
        messages={messages}
        pageFirstElRef={pageFirstElRef}
        listElRef={listElRef}
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
