import styled from '@emotion/styled';

import { StatusTypeEnum, type StatusType } from '../../types/statusType';

import type { myTheme } from '../../types/theme';

interface MentoringApplicationStatusProps {
  status: StatusType;
}

function StatusEmoticon({ status }: MentoringApplicationStatusProps) {
  const getEmoticon = (status: StatusType) => {
    switch (status) {
      case StatusTypeEnum.pending:
        return '⏳';
      case StatusTypeEnum.approved:
        return '✅';
      case StatusTypeEnum.completed:
        return '🎉';
      case StatusTypeEnum.rejected:
        return '❌';
      default:
        return null;
    }
  };

  return <span>{getEmoticon(status)}</span>;
}

function MentoringApplicationStatus({
  status,
}: MentoringApplicationStatusProps) {
  return (
    <StyledContainer status={status}>
      <StatusEmoticon status={status} />
      <span>{status}</span>
    </StyledContainer>
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
  승인대기: {
    border: (theme) => theme.OUTLINE.YELLOW,
    background: (theme) => theme.BG.YELLOW,
    color: (theme) => theme.FONT.Y01,
  },
  승인됨: {
    border: (theme) => theme.SYSTEM.MAIN300,
    background: (theme) => theme.SYSTEM.MAIN100,
    color: (theme) => theme.SYSTEM.MAIN800,
  },
  완료됨: {
    border: (theme) => theme.SYSTEM.MAIN500,
    background: (theme) => theme.SYSTEM.MAIN200,
    color: (theme) => theme.SYSTEM.MAIN900,
  },
  거절됨: {
    border: (theme) => theme.FONT.ERROR,
    background: (theme) => theme.BG.GRAY,
    color: (theme) => theme.FONT.W01,
  },
} as const;

const StyledContainer = styled.p<MentoringApplicationStatusProps>`
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
