import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

import { CHAT_MESSAGES, CHAT_ROOM_INFO } from './data';

const getChatRoom = http.get(
  `*${API_ENDPOINTS.CHATROOMS}/:chatroomId/messages`,
  () => {
    return HttpResponse.json(CHAT_MESSAGES);
  },
);

const getChatRoomInfo = http.get(
  `*${API_ENDPOINTS.CHATROOMS}/:chatroomId`,
  () => {
    return HttpResponse.json(CHAT_ROOM_INFO);
  },
);

export const chatRoomHandler = [getChatRoom, getChatRoomInfo];
