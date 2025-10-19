import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { MessageResponse } from '../types/message';

export const getChatRoom = async ({
  chatRoomId,
  cursorCode,
}: {
  chatRoomId: number;
  cursorCode?: string | null;
}) => {
  return await apiClient.get<MessageResponse>({
    endpoint: `${API_ENDPOINTS.CHATROOMS}/${chatRoomId}/messages`,
    withCredentials: true,
    ...(cursorCode ? { searchParams: { cursorCode } } : {}),
  });
};
