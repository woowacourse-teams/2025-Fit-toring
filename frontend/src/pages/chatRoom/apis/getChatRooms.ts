import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';
import { Message } from '../types/message';

export const getChatRooms = async (chatroomId: number) => {
  return await apiClient.get<Message[]>({
    endpoint: `${API_ENDPOINTS.CHATROOMS}/${chatroomId}/messages`,
  });
};
