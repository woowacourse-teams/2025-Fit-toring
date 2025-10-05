import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

import { CHAT_MESSAGES } from './data';

const getChatRoom = http.get(
  `*${API_ENDPOINTS.CHATROOMS}/:chatroomId/messages`,
  () => {
    return HttpResponse.json(CHAT_MESSAGES);
  },
);

export const chatRoomHandler = [getChatRoom];
