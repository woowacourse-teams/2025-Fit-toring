import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { Message } from '../types/message';

export const getChatRoomInfo = async (chatRoomId: number) => {
  return await apiClient.get<Message[]>({
    endpoint: `${API_ENDPOINTS.CHATROOMS}/${chatRoomId}`,
    withCredentials: true,
  });
};
