import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { Message } from '../types/message';

export const getChatRoom = async ({
  chatRoomId,
  sortKey,
  cursorCode,
}: {
  chatRoomId: number;
  sortKey: string;
  cursorCode?: string;
}) => {
  const searchParams: Record<string, string> = { sortKey };

  if (cursorCode) {
    searchParams.cursorCode = cursorCode;
  }

  return await apiClient.get<Message[]>({
    endpoint: `${API_ENDPOINTS.CHATROOMS}/${chatRoomId}/messages`,
    withCredentials: true,
    searchParams,
  });
};
