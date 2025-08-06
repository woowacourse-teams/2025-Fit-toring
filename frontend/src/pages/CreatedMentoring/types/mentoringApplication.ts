import type { StatusType } from './statusType';

export interface MentoringApplication {
  id: number;
  name: string;
  phoneNumber: string | null; // 승인 대기중, 완료됨이면 null, 승인됨이면 전화번호 제공
  price: number;
  content: string;
  status: StatusType;
  applicationDate: string; // 신청일
  scheduledDate: string | null; // 예정일
  completionDate: string | null; // 완료일
}
