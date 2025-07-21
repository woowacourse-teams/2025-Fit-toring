import styled from '@emotion/styled';

function Slogan() {
  return (
    <StyledContainer>
      <StyledWrapper>
        <StyledTitle>
          내가 알고 싶은 운동 & 식단, <StyledStrong>핏토링</StyledStrong>에서
          물어봐요!
        </StyledTitle>
        <StyledDescription>
          전문 트레이너와 운동 애호가들로부터 15분 단위로 합리적인 비용의 1회성
          멘토링을 받아보세요
        </StyledDescription>
      </StyledWrapper>
    </StyledContainer>
  );
}

export default Slogan;

const StyledContainer = styled.section`
  display: flex;
  width: 100%;
  height: auto;
  padding: 3rem 1.4rem;
  justify-content: center;
  align-items: center;

  border-bottom: 1px solid ${({ theme }) => theme.LINE.REGULAR};

  background: linear-gradient(
    180deg,
    ${({ theme }) => theme.SYSTEM.MAIN50} 0%,
    ${({ theme }) => theme.BG.WHITE} 100%
  );
`;

const StyledWrapper = styled.div`
  display: flex;
  height: auto;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 1.5rem;
`;

const StyledTitle = styled.h1`
  width: 23.2rem;

  text-align: center;

  ${({ theme }) => theme.TYPOGRAPHY.H2_R}
`;

const StyledStrong = styled.strong`
  color: ${({ theme }) => theme.SYSTEM.MAIN700};
`;

const StyledDescription = styled.p`
  width: 28.2rem;

  color: ${({ theme }) => theme.FONT.B03};
  text-align: center;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
