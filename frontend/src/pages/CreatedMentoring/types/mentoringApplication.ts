import type { StatusType } from './statusType';

export interface MentoringApplication {
  id: number;
  name: string;
  phoneNumber: string | null;
  price: number;
  content: string;
  status: StatusType;
  applicationDate: string;
  scheduledDate: string | null;
  completionDate: string | null;
}
