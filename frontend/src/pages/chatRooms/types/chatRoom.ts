import type { StatusType } from '../../../common/types/statusType';

export interface ChatRoom {
  chatRoomId: number;
  profileImageUrl: string | null;
  opponentName: string;
  reservationStatus: StatusType;
  lastChatContent: string;
  lastChatCreatedAt: string;
}
