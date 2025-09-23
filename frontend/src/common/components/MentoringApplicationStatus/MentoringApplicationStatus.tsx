import styled from '@emotion/styled';

import { StatusTypeEnum, type StatusType } from '../../types/statusType';

import type { myTheme } from '../../types/theme';

interface MentoringApplicationStatusProps {
  status: StatusType;
}

const STATUS_DESCRIPTION = {
  [StatusTypeEnum.PENDING]: {
    VALUE: '승인대기',
    EMOTICON: '⏳',
  },
  [StatusTypeEnum.APPROVED]: {
    VALUE: '승인됨',
    EMOTICON: '✅',
  },
  [StatusTypeEnum.COMPLETE]: {
    VALUE: '완료됨',
    EMOTICON: '🎉',
  },
  [StatusTypeEnum.REJECTED]: {
    VALUE: '거절됨',
    EMOTICON: '❌',
  },
} as const;

function MentoringApplicationStatus({
  status,
}: MentoringApplicationStatusProps) {
  return (
    <S_Container status={status}>
      <span>{STATUS_DESCRIPTION[status].EMOTICON}</span>
      <span>{STATUS_DESCRIPTION[status].VALUE}</span>
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
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;

  width: 8.2rem;
  padding: 0.3rem 0.6rem;
  border: 1px solid ${({ theme, status }) => statusStyles[status].border(theme)};
  border-radius: 8px;

  background-color: ${({ theme, status }) =>
    statusStyles[status].background(theme)};

  color: ${({ theme, status }) => statusStyles[status].color(theme)};

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
