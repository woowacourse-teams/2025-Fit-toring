import styled from '@emotion/styled';

function Slogan() {
  return (
    <StyledContainer>
      <StyledTitle>
        내가 알고 싶은 운동 & 식단, <StyledStrong>핏토링</StyledStrong>에서
        물어봐요!
      </StyledTitle>
      <StyledDescription>
        전문 트레이너와 운동 애호가들로부터 15분 단위로 합리적인 비용의 1회성
        멘토링을 받아보세요
      </StyledDescription>
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

  text-align: center;

  ${({ theme }) => theme.TYPOGRAPHY.H2_R}
  color: ${({ theme }) => theme.FONT.B01};
`;

const StyledStrong = styled.strong`
  color: ${({ theme }) => theme.SYSTEM.MAIN600};
`;

const StyledDescription = styled.p`
  width: 28.2rem;

  color: ${({ theme }) => theme.FONT.B03};
  text-align: center;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
