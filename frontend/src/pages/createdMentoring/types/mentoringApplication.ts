import type { StatusType } from './statusType';

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
