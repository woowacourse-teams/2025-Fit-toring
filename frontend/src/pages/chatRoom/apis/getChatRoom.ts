import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import type { MessageResponse } from '../types/message';

export const getChatRoom = async ({
  chatRoomId,
  sortKey,
  cursorCode,
}: {
  chatRoomId: number;
  sortKey: string;
  cursorCode?: string | null;
}) => {
  const searchParams: Record<string, string> = { sortKey };

  if (cursorCode) {
    searchParams.cursorCode = cursorCode;
  }

  return await apiClient.get<MessageResponse>({
    endpoint: `${API_ENDPOINTS.CHATROOMS}/${chatRoomId}/messages`,
    withCredentials: true,
    searchParams,
  });
};
