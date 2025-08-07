import type { StatusType } from '../../../common/types/statusType';

export interface MentoringApplication {
  reservationId: number;
  menteeName: string;
  phoneNumber: string | null;
  price: number;
  content: string;
  status: StatusType;
  createdAt: string;
}

export interface MentoringApplicationResponse {
  data: MentoringApplication[];
  statusCode: number;
}
