export type StatusType = '승인대기' | '승인됨' | '완료됨' | '거절됨';

export enum StatusTypeEnum {
  pending = '승인대기',
  approved = '승인됨',
  completed = '완료됨',
  rejected = '거절됨',
}
