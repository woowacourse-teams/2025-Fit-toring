import type { MESSAGE_TYPE } from '../constants/message';

interface MessageDTO {
  // 서버와 클라이언트 공통
  content: string | null;
  createdAt: string;
  senderId: number;
  chatRoomId: number;
  tempId: number;
  messageType: MessageType;
  thumbnailUrl: string | null;
  originalImageUrl: string | null;
  // 서버에서 받는 id
  chatMessageId?: number;

  // 클라이언트의 상태 관리를 위한 속성
  status?: 'success' | 'fail' | 'pending';
}

export type Message = ImageMessage | TextMessage;

export interface ImageMessage extends MessageDTO {
  content: null;
  messageType: 'IMAGE';
  thumbnailUrl: string;
  originalImageUrl: string;
}

export interface TextMessage extends MessageDTO {
  content: string;
  messageType: 'TEXT';
  thumbnailUrl: null;
  originalImageUrl: null;
}

export interface MessageResponse {
  chatMessages: Message[];
  nextCursorCode: string | null;
  hasNext: boolean;
}

export type MessageType = (typeof MESSAGE_TYPE)[keyof typeof MESSAGE_TYPE];
