import type { StatusType } from '../../../common/types/statusType';

export interface ChatRooms {
  chatRoomId: number;
  profileImageUrl: string | null;
  opponentName: string;
  reservationStatus: StatusType;
  lastChatContent: string;
  lastChatCreatedAt: string;
}
