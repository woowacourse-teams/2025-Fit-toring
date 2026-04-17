import type { MESSAGE_TYPE } from '../constants/message';

export interface Message {
  // 서버와 클라이언트 공통
  content: string;
  createdAt: string;
  senderId: number;
  chatRoomId: number;
  tempId: number;
  messageType: MessageType;
  // 서버에서 받는 id
  chatMessageId?: number;

  // 클라이언트의 상태 관리를 위한 속성
  status?: 'success' | 'fail' | 'pending';
  phase?: 'normal' | 'before-refresh' | 'during-reconnect';
}

export interface MessageResponse {
  chatMessages: Message[];
  nextCursorCode: string | null;
  hasNext: boolean;
}

export type MessageType = (typeof MESSAGE_TYPE)[keyof typeof MESSAGE_TYPE];
