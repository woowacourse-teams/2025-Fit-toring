import type { StatusType } from '../../../common/types/statusType';

export interface ParticipatedMentoringType {
  reservationId: number;
  mentorName: string;
  mentorProfileImage: string;
  price: string;
  reservedAt: string;
  categories: string[];
  isReviewed: boolean;
  status: StatusType;
}
