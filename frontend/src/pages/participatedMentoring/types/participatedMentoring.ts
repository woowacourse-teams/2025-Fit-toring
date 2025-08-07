import type { StatusType } from '../../../common/types/statusType';

export interface ParticipatedMentoringType {
  mentorName: string;
  mentorProfileImage: string;
  price: string;
  reservedAt: string;
  categories: string[];
  isReviewed: boolean;
  status: StatusType;
}
