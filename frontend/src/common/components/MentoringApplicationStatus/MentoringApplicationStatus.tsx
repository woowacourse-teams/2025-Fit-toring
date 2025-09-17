import styled from '@emotion/styled';

import { StatusTypeEnum, type StatusType } from '../../types/statusType';

import type { myTheme } from '../../types/theme';

interface MentoringApplicationStatusProps {
  status: StatusType;
  type?: 'CREATED' | 'PARTICIPATED';
}

const STATUS_DESCRIPTION = {
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
} as const;

const STATUS_PARTICIPATED_DESCRIPTION = {
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
} as const;

function MentoringApplicationStatus({
  status,
  type = 'CREATED',
}: MentoringApplicationStatusProps) {
  const description =
    type === 'CREATED'
      ? STATUS_DESCRIPTION[status]
      : STATUS_PARTICIPATED_DESCRIPTION[status];
  return (
    <S_Container status={status}>
      <span>{description.VALUE}</span>
    </S_Container>
  );
}

export default MentoringApplicationStatus;

const statusStyles: Record<
  StatusType,
  {
    border: (theme: myTheme) => string;
    background: (theme: myTheme) => string;
    color: (theme: myTheme) => string;
  }
> = {
  PENDING: {
    border: (theme) => theme.OUTLINE.YELLOW,
    background: (theme) => theme.BG.YELLOW,
    color: (theme) => theme.FONT.Y01,
  },
  APPROVED: {
    border: (theme) => theme.SYSTEM.MAIN300,
    background: (theme) => theme.SYSTEM.MAIN100,
    color: (theme) => theme.SYSTEM.MAIN800,
  },
  COMPLETE: {
    border: (theme) => theme.SYSTEM.MAIN500,
    background: (theme) => theme.SYSTEM.MAIN200,
    color: (theme) => theme.SYSTEM.MAIN900,
  },
  REJECTED: {
    border: (theme) => theme.FONT.ERROR,
    background: (theme) => theme.BG.RED,
    color: (theme) => theme.FONT.W01,
  },
} as const;

const S_Container = styled.p<MentoringApplicationStatusProps>`
  color: ${({ theme }) => theme.SYSTEM.MAIN500};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
