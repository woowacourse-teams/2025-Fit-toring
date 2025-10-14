import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

import {
  CHAT_MESSAGES1,
  CHAT_MESSAGES2,
  CHAT_MESSAGES3,
  CHAT_MESSAGES4,
  CHAT_ROOM_INFO,
} from './data';

const mockPages = {
  null: CHAT_MESSAGES1,
  '1758712152|30': CHAT_MESSAGES2,
  '1758712152|20': CHAT_MESSAGES3,
  '1758712152|10': CHAT_MESSAGES4,
};

export const getChatRoom = http.get(
  `*${API_ENDPOINTS.CHATROOMS}/:chatroomId/messages`,
  ({ request }) => {
    const url = new URL(request.url);
    const cursorCode = url.searchParams.get('cursorCode');

    const data = mockPages[cursorCode ?? 'null'];

    if (!data) {
      return HttpResponse.json(
        { chatMessages: [], nextCursorCode: null, hasNext: false },
        { status: 200 },
      );
    }

    return HttpResponse.json(data, { status: 200 });
  },
);
const getChatRoomInfo = http.get(
  `*${API_ENDPOINTS.CHATROOMS}/:chatroomId`,
  () => {
    return HttpResponse.json(CHAT_ROOM_INFO);
  },
);

export const chatRoomHandler = [getChatRoom, getChatRoomInfo];
