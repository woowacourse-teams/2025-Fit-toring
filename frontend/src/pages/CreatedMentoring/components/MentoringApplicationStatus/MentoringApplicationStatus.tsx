import styled from '@emotion/styled';

import type { myTheme } from '../../../../common/types/theme';

interface MentoringApplicationStatusProps {
  status: '승인 대기' | '승인됨' | '완료됨';
}

function StatusEmoticon({ status }: MentoringApplicationStatusProps) {
  const getEmoticon = (status: string) => {
    switch (status) {
      case '승인 대기':
        return '⏳';
      case '승인됨':
        return '✅';
      case '완료됨':
        return '🎉';
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

const statusStyles = {
  '승인 대기': {
    border: (theme: myTheme) => theme.OUTLINE.YELLOW,
    background: (theme: myTheme) => theme.BG.YELLOW,
    color: (theme: myTheme) => theme.FONT.Y01,
  },
  승인됨: {
    border: (theme: myTheme) => theme.SYSTEM.MAIN300,
    background: (theme: myTheme) => theme.SYSTEM.MAIN100,
    color: (theme: myTheme) => theme.SYSTEM.MAIN700,
  },
  완료됨: {
    border: (theme: myTheme) => theme.SYSTEM.MAIN400,
    background: (theme: myTheme) => theme.SYSTEM.MAIN200,
    color: (theme: myTheme) => theme.SYSTEM.MAIN700,
  },
} as const;

const StyledContainer = styled.p<MentoringApplicationStatusProps>`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;

  width: fit-content;
  padding: 0.3rem 0.6rem;
  border: 1px solid ${({ theme, status }) => statusStyles[status].border(theme)};
  border-radius: 8px;

  background-color: ${({ theme, status }) =>
    statusStyles[status].background(theme)};

  color: ${({ theme, status }) => statusStyles[status].color(theme)};

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
