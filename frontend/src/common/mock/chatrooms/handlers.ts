import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

import {
  CHAT_MESSAGES1,
  CHAT_MESSAGES2,
  CHAT_MESSAGES3,
  CHAT_MESSAGES4,
  CHAT_MESSAGES5,
  CHAT_ROOM_INFO,
} from './data';

import type { MessageResponse } from '../../../pages/chatRoom/types/message';

const mockPages: Record<string, MessageResponse> = {
  null: CHAT_MESSAGES1,
  '1758712152|30': CHAT_MESSAGES2,
  '1758712152|20': CHAT_MESSAGES3,
  '1758712152|10': CHAT_MESSAGES4,
  '1758712152|0': CHAT_MESSAGES5,
};

const BASE_URL = process.env.API_BASE_URL;
const CHATROOM_URL = `${BASE_URL}${API_ENDPOINTS.CHATROOMS}/:chatroomId/messages`;

export const getChatRoom = http.get(`${CHATROOM_URL}`, ({ request }) => {
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
});

const CHATROOM_INFO_URL = `${BASE_URL}${API_ENDPOINTS.CHATROOMS}/:chatroomId`;
const getChatRoomInfo = http.get(`${CHATROOM_INFO_URL}`, () => {
  return HttpResponse.json(CHAT_ROOM_INFO);
});

export const chatRoomHandler = [getChatRoom, getChatRoomInfo];
