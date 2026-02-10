import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { ChatRooms } from '../types/chatRooms';

export const getChatRooms = async () => {
  return await apiClient.get<ChatRooms[]>({
    endpoint: `${API_ENDPOINTS.CHATROOMS}`,
    withCredentials: true,
  });
};
