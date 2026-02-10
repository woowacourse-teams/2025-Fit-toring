import type { StatusType } from '../../../common/types/statusType';

export interface ChatRooms {
  chatRoomId: number;
  profileImageUrl: string;
  opponentName: string;
  reservationStatus: StatusType;
  lastChatContent: string;
  lastChatCreatedAt: string;
}
