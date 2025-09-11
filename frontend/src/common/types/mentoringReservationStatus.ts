import { StatusTypeEnum } from './statusType';

import type { StatusType } from './statusType';

export type MentoringReservationStatusType = Exclude<StatusType, 'REJECTED'>;

export enum MentoringReservationStatusTypeEnum {
  PENDING = StatusTypeEnum.PENDING,
  APPROVED = StatusTypeEnum.APPROVED,
  COMPLETE = StatusTypeEnum.COMPLETE,
}
