import styled from '@emotion/styled';

import { StatusTypeEnum, type StatusType } from '../../types/statusType';

import type { myTheme } from '../../types/theme';

interface MentoringApplicationStatusProps {
  status: StatusType;
  type?: 'CREATED' | 'PARTICIPATED';
}

const STATUS_DESCRIPTION = {
  CREATED: {
    [StatusTypeEnum.PENDING]: {
      VALUE: '승인대기',
    },
    [StatusTypeEnum.APPROVED]: {
      VALUE: '승인됨',
    },
    [StatusTypeEnum.COMPLETE]: {
      VALUE: '완료됨',
    },
    [StatusTypeEnum.REJECTED]: {
      VALUE: '거절됨',
    },
  },
  PARTICIPATED: {
    [StatusTypeEnum.PENDING]: {
      VALUE: '예약신청',
    },
    [StatusTypeEnum.APPROVED]: {
      VALUE: '예약확정',
    },
    [StatusTypeEnum.COMPLETE]: {
      VALUE: '완료됨',
    },
    [StatusTypeEnum.REJECTED]: {
      VALUE: '거절됨',
    },
  },
} as const;

function MentoringApplicationStatus({
  status,
  type = 'CREATED',
}: MentoringApplicationStatusProps) {
  const description = STATUS_DESCRIPTION[type][status];
  return (
    <S_Container status={status}>
      <span>{description.VALUE}</span>
    </S_Container>
  );
}

export default MentoringApplicationStatus;

const S_Container = styled.p<MentoringApplicationStatusProps>`
  color: ${({ theme, status }) =>
    status === StatusTypeEnum.REJECTED
      ? theme.SYSTEM.GRAY600
      : theme.SYSTEM.MAIN500};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
