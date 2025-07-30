import styled from '@emotion/styled';

function Slogan() {
  return (
    <StyledContainer>
      <StyledTitle>
        내가 알고 싶은 운동 & 식단, <StyledStrong>핏토링</StyledStrong>에서
        물어봐요!
      </StyledTitle>
    </StyledContainer>
  );
}

export default Slogan;

const StyledContainer = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.5rem;

  width: 100%;
  height: auto;
  padding: 3rem 1.4rem;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};

  background: linear-gradient(
    180deg,
    ${({ theme }) => theme.SYSTEM.MAIN50} 0%,
    ${({ theme }) => theme.BG.WHITE} 100%
  );
`;

const StyledTitle = styled.h1`
  width: 23.2rem;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.H2_R}
`;

const StyledStrong = styled.strong`
  color: ${({ theme }) => theme.SYSTEM.MAIN600};
`;
