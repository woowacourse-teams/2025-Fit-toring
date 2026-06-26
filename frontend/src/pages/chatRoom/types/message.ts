import type { MESSAGE_TYPE } from '../constants/message';

interface MessageDTO {
  // 서버와 클라이언트 공통
  content: string | null;
  createdAt: string;
  senderId: number;
  chatRoomId: number;
  tempId: number | null;
  messageType: MessageType;
  thumbnailUrl?: string | null;
  originalImageUrl?: string | null;
  // WebSocket 응답에서 받는 멱등키
  messageId?: string;
  // 서버에서 받는 id
  chatMessageId?: number;

  // 클라이언트의 상태 관리를 위한 속성
  status?: 'success' | 'fail' | 'pending';
  phase?: 'normal' | 'before-refresh' | 'during-reconnect';
}

export type Message = ImageMessage | TextMessage;

export interface ImageMessage extends MessageDTO {
  content: null;
  messageType: 'IMAGE';
  thumbnailUrl: string | null;
  originalImageUrl: string;
  // 클라이언트 전용: 전송 확정 식별자(서버 발급). pending 메시지에만 임시 보관한다.
  uploadId?: string;
}

export interface TextMessage extends MessageDTO {
  content: string;
  messageType: 'TEXT';
  thumbnailUrl?: null;
  originalImageUrl?: null;
}

export interface MessageResponse {
  chatMessages: Message[];
  nextCursorCode: string | null;
  hasNext: boolean;
}

export type MessageType = (typeof MESSAGE_TYPE)[keyof typeof MESSAGE_TYPE];
