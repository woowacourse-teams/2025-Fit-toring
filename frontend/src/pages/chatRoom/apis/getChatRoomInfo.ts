import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { ChatRoomInfo } from '../types/chatRoomInfo';

export const getChatRoomInfo = async (chatRoomId: number) => {
  return await apiClient.get<ChatRoomInfo>({
    endpoint: `${API_ENDPOINTS.CHATROOMS}/${chatRoomId}`,
    withCredentials: true,
  });
};
