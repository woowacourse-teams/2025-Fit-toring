import type { StatusType } from '../../../common/types/statusType';

export interface ParticipatedMentoringType {
  reservationId: number;
  mentoringId: number;
  mentorName: string;
  mentorProfileImage: string;
  reservedAt: string;
  content: string;
  status: StatusType;
  isReviewed: boolean;
  chatRoomId: number | null;
}
