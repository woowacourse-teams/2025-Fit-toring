import styled from '@emotion/styled';

import { StatusTypeEnum, type StatusType } from '../../types/statusType';

interface MentoringApplicationStatusProps {
  status: StatusType;
  type?: 'CREATED' | 'PARTICIPATED';
}

const STATUS_DESCRIPTION = {
  CREATED: {
    [StatusTypeEnum.PENDING]: {
      VALUE: '대기중',
    },
    [StatusTypeEnum.APPROVED]: {
      VALUE: '승인 확정',
    },
    [StatusTypeEnum.COMPLETE]: {
      VALUE: '완료',
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

const CREATED_STATUS_COLOR: Record<StatusType, string> = {
  [StatusTypeEnum.PENDING]: '#92400E',
  [StatusTypeEnum.APPROVED]: '#006760',
  [StatusTypeEnum.COMPLETE]: '#475569',
  [StatusTypeEnum.REJECTED]: '#B91C1C',
};

function MentoringApplicationStatus({
  status,
  type = 'CREATED',
}: MentoringApplicationStatusProps) {
  const description = STATUS_DESCRIPTION[type][status];
  return (
    <S_Container status={status} type={type}>
      <span>{description.VALUE}</span>
    </S_Container>
  );
}

export default MentoringApplicationStatus;

const S_Container = styled.p<MentoringApplicationStatusProps>`
  color: ${({ theme, status, type }) => {
    if (type === 'CREATED') {
      return CREATED_STATUS_COLOR[status];
    }
    return status === StatusTypeEnum.REJECTED
      ? theme.SYSTEM.GRAY600
      : theme.SYSTEM.MAIN500;
  }};

  ${({ type }) =>
    type === 'CREATED'
      ? `
    font-size: 1.25rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
  `
      : ''}

  ${({ theme, type }) => (type === 'CREATED' ? '' : theme.TYPOGRAPHY.B2_R)}
`;
