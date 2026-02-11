import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { ChatRoom } from '../types/chatRoom';

export const getChatRooms = async () => {
  return await apiClient.get<ChatRoom[]>({
    endpoint: `${API_ENDPOINTS.CHATROOMS}`,
    withCredentials: true,
  });
};
