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

import ApiError from '../../common/apis/ApiError';
import { postReissue } from '../../common/apis/postReissue';
import { PAGE_URL } from '../../common/constants/url';
import {
  hideChannelTalk,
  showChannelTalk,
} from '../../common/utils/channelTalk';

import { getChatRoomInfo } from './apis/getChatRoomInfo';
import ChatContent from './components/ChatContent/ChatContent';
import ChatRoomForbidden from './components/ChatRoomForbidden/ChatRoomForbidden';
import ChatRoomHeader from './components/ChatRoomHeader/ChatRoomHeader';
import ChatRoomInfoSkeleton from './components/ChatRoomInfoSkeleton/ChatRoomInfoSkeleton';
import InputSection from './components/InputSection/InputSection';
import MentoringActionPanel from './components/MentoringActionPanel/MentoringActionPanel';
import { MESSAGE_TYPE } from './constants/message';
import useDelayedVisibility from './hooks/useDelayedVisibility';
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

  const visible = useDelayedVisibility(1000);

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

  const firstId = messages[0]?.chatMessageId ?? null;
  const lastId = messages[messages.length - 1]?.chatMessageId ?? null;

  const { capturePrevScroll } = useScrollToBottomOnMessageSend({
    firstId,
    lastId,
    listElRef,
  });

  const isReconnectPendingRef = useRef(false);

  const handleMessageSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (message === '' || memberId === null) {
      return;
    }

    capturePrevScroll();

    const tempId = Date.now();

    const optimisticMsg: Message = {
      senderId: Number(memberId),
      content: message,
      createdAt: new Date().toString(),
      chatRoomId: Number(chatRoomId),
      chatMessageId: tempId,
      tempId,
      status: 'pending' as const,
      messageType: MESSAGE_TYPE.TEXT,
      phase: 'normal',
    };

    const client = stompClientRef.current;

    if (isReconnectPendingRef.current) {
      const reconnectMsg = {
        ...optimisticMsg,
        phase: 'during-reconnect' as const,
      };

      setMessages((prev) => [...prev, reconnectMsg]);
      setMessage('');
      return;
    }

    if (!client || !client.connected) {
      return;
    }

    setMessages((prev) => [...prev, optimisticMsg]);
    setMessage('');

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

  useEffect(() => {
    hideChannelTalk();

    return () => {
      showChannelTalk();
    };
  }, []);

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

  const isRefreshingRef = useRef(false);

  useEffect(() => {
    const apiBaseUrl = process.env.API_BASE_URL ?? '';
    const wsBaseUrl = apiBaseUrl.replace(/^http/, 'ws');
    const wsChatUrl = `${wsBaseUrl}/ws-chat`;

    const client = new Client({
      webSocketFactory: () => {
        console.log('[WebSocket] webSocketFactory called');
        return new WebSocket(wsChatUrl);
      },
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      reconnectDelay: 5000,
      onStompError: async (frame) => {
        const parsedBody = JSON.parse(frame.body);

        if (parsedBody.code === 'TOKEN_EXPIRED') {
          if (isRefreshingRef.current) {
            return;
          }

          isRefreshingRef.current = true;
          isReconnectPendingRef.current = true;

          setMessages((prev) =>
            prev.map((msg) => {
              if (msg.status !== 'pending') {
                return msg;
              }
              if (msg.phase === 'during-reconnect') {
                return msg;
              }

              return { ...msg, phase: 'before-refresh' };
            }),
          );

          try {
            await postReissue();

            if (client.active) {
              await client.deactivate();
            }
            client.activate();
          } catch (e) {
            isReconnectPendingRef.current = false;
            navigate(PAGE_URL.LOGIN);
            console.error('토큰 재발급 실패:', e);
          } finally {
            isRefreshingRef.current = false;
          }
        }
      },

      onWebSocketError: (e) => console.error('WebSocket error:', e),
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
                    phase: 'normal',
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

              return [
                ...prev,
                { ...parsedMessage, status: 'success', phase: 'normal' },
              ];
            });
          },
        );

        client.subscribe('/user/queue/errors', (message: IMessage) => {
          const parsedErrorMessage = JSON.parse(message.body);

          setMessages((prev) => {
            const nextMessages = prev.map((msg) => {
              if (msg.tempId === parsedErrorMessage.tempId) {
                return {
                  ...msg,
                  status: 'fail' as const,
                  phase: 'normal' as const,
                };
              }
              return msg;
            });
            messagesRef.current = nextMessages;
            return nextMessages;
          });
        });

        const pendingToResend = messagesRef.current.filter(
          (msg) => msg.status === 'pending' && msg.phase !== 'normal',
        );

        if (pendingToResend.length > 0) {
          pendingToResend.forEach((msg) => {
            client.publish({
              destination: `/app/chatroom/${chatRoomId}`,
              body: JSON.stringify({
                content: msg.content,
                tempId: msg.tempId,
                messageType: msg.messageType,
              }),
            });
          });
        }

        isReconnectPendingRef.current = false;
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
        <S_LoadingHeaderArea>
          <S_LoadingHeaderWrapper visible={visible} aria-hidden>
            <ChatRoomInfoSkeleton />
          </S_LoadingHeaderWrapper>
        </S_LoadingHeaderArea>
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

const S_LoadingHeaderArea = styled.div`
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_LoadingHeaderWrapper = styled.div<{ visible: boolean }>`
  visibility: ${({ visible }) => (visible ? 'visible' : 'hidden')};
`;
