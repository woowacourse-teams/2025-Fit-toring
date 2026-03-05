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
import Button from '../../common/components/Button/Button';
import Modal from '../../common/components/Modal/Modal';
import { PAGE_URL } from '../../common/constants/url';
import useS3Upload from '../../common/hooks/useS3Upload';
import {
  hideChannelTalk,
  showChannelTalk,
} from '../../common/utils/channelTalk';

import { getChatRoomInfo } from './apis/getChatRoomInfo';
import ChatContent from './components/ChatContent/ChatContent';
import ChatRoomForbidden from './components/ChatRoomForbidden/ChatRoomForbidden';
import ChatRoomHeader from './components/ChatRoomHeader/ChatRoomHeader';
import ChatRoomInfoSkeleton from './components/ChatRoomInfoSkeleton/ChatRoomInfoSkeleton';
import ChatRoomInputArea from './components/ChatRoomInputArea/ChatRoomInputArea';
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
          if (isRefreshingRef.current) {
            return;
          }

          isRefreshingRef.current = true;

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
            isRefreshingRef.current = false;
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

        client.subscribe('/user/queue/errors', (message: IMessage) => {
          const parsedErrorMessage = JSON.parse(message.body);

          setMessages((prev) => {
            return prev.map((message) => {
              if (message.tempId === parsedErrorMessage.tempId) {
                return { ...message, status: 'fail' };
              }
              return message;
            });
          });
        });

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

  const [image, setImage] = useState<File | null>(null);

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files || e.target.files.length === 0) {
      setImage(null);
      return;
    }
    const file = e.target.files[0];
    setImage(file);
  };

  const resetImageInput = () => {
    setImage(null);
  };

  const { uploadFile } = useS3Upload();

  const handleImageSubmit = async () => {
    if (!image) {
      return;
    }
    const imageForUpload = image;
    resetImageInput();

    const { uploadedUrl } = await uploadFile(imageForUpload, 'CHAT');

    if (!uploadedUrl) {
      alert('이미지 업로드에 실패했습니다. 다시 시도해주세요.');
      return;
    }

    const tempId = Date.now();

    const optimisticMsg = {
      senderId: Number(memberId),
      content: uploadedUrl,
      createdAt: new Date().toString(),
      chatRoomId: Number(chatRoomId),
      chatMessageId: tempId,
      tempId,
      status: 'pending' as const,
      messageType: MESSAGE_TYPE.IMAGE,
    };

    setMessages((prev) => [
      ...prev,
      {
        ...optimisticMsg,
      },
    ]);

    const client = stompClientRef.current;
    if (!client || !client.connected || memberId === null) {
      return;
    }

    client.publish({
      destination: `/app/chatroom/${chatRoomId}`,
      body: JSON.stringify({
        content: uploadedUrl,
        tempId,
        messageType: MESSAGE_TYPE.IMAGE,
      }),
    });
  };

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
      <ChatRoomInputArea
        value={message}
        onChange={handleChange}
        onSubmit={handleMessageSubmit}
        onImageChange={handleImageChange}
      />

      <Modal opened={image !== null} onCloseClick={resetImageInput}>
        <S_ImageSendContainer>
          <S_ImageSendTitle>이미지 전송</S_ImageSendTitle>
          {image && (
            <S_ImagePreview src={URL.createObjectURL(image)} alt="미리보기" />
          )}
          <S_ButtonWrapper>
            <S_CancelButton onClick={resetImageInput} size="full">
              취소
            </S_CancelButton>
            <S_SendButton onClick={handleImageSubmit} size="full">
              전송
            </S_SendButton>
          </S_ButtonWrapper>
        </S_ImageSendContainer>
      </Modal>
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

const S_ImageSendContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.8rem;
`;

const S_ImageSendTitle = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R};
`;

const S_ImagePreview = styled.img`
  width: 100%;
  height: auto;
  aspect-ratio: 1 / 1;
  object-fit: cover;
`;

const S_ButtonWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;

  width: 100%;
  padding: 0.4rem 0;
`;

const S_CancelButton = styled(Button)`
  border: 1px solid ${({ theme }) => theme.OUTLINE.BLACK};

  background-color: ${({ theme }) => theme.BG.WHITE};

  color: ${({ theme }) => theme.FONT.B01};
`;

const S_SendButton = styled(Button)`
  border: 1px solid ${({ theme }) => theme.OUTLINE.BLACK};

  background-color: ${({ theme }) => theme.BG.BLACK};
`;
