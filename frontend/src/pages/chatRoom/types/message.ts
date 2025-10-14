export interface Message {
  // 서버와 클라이언트 공통
  content: string;
  createdAt: string;
  senderId: number;
  chatRoomId: number;
  tempId: number;

  // 서버에서 받는 id
  chatMessageId?: number;

  // 클라이언트의 상태 관리를 위한 속성
  status?: 'success' | 'fail' | 'pending';
}

export interface MessageResponse {
  chatMessages: Message[];
  nextCursorCode: string;
  hasNext: boolean;
}
