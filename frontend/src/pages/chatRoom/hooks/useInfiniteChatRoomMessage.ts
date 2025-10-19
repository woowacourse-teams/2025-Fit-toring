import {
  useInfiniteQuery,
  type InfiniteData,
  type QueryKey,
} from '@tanstack/react-query';

import { getChatRoomMessages } from '../apis/getChatRoom';

import type { MessageResponse } from '../types/message';

interface ChatRoomPageParam {
  chatRoomId: number;
  sortKey: string;
  cursorCode?: string | null;
}

const useInfiniteChatRoomMessage = (chatRoomId: number) => {
  return useInfiniteQuery<
    MessageResponse,
    Error,
    InfiniteData<MessageResponse>,
    QueryKey,
    ChatRoomPageParam
  >({
    queryKey: ['chatRoom', chatRoomId],
    queryFn: async ({ pageParam }) => {
      return getChatRoomMessages(pageParam);
    },
    initialPageParam: {
      chatRoomId: Number(chatRoomId),
      sortKey: 'CREATED_AT',
    },
    getNextPageParam: (lastPage) => {
      if (lastPage.hasNext) {
        return {
          chatRoomId: Number(chatRoomId),
          cursorCode: lastPage.nextCursorCode,
          sortKey: 'CREATED_AT',
        };
      }
      return undefined;
    },
    select: (data) => ({
      ...data,
      pages: data.pages
        .slice()
        .reverse()
        .map((page) => ({
          ...page,
          chatMessages: [...page.chatMessages].reverse(),
        })),
    }),
  });
};

export default useInfiniteChatRoomMessage;
