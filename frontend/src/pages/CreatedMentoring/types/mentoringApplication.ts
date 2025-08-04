export interface MentoringApplication {
  id: number;
  name: string;
  phoneNumber: string | null; // 승인 대기중, 완료됨이면 null, 승인됨이면 전화번호 제공
  fee: number;
  content: string;
  status: '승인 대기' | '승인됨' | '완료됨';
  applicationDate: string; // 신청일
  scheduledDate: string | null; // 예정일
  completionDate: string | null; // 완료일
}
