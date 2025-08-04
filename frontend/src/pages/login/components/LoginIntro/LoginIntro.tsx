import styled from '@emotion/styled';

import logo from '../../../../common/assets/images/logo.svg';

function LoginIntro() {
  return (
    <StyledContainer>
      <StyledImgWrapper>
        <StyledImg src={logo} alt="핏토링 메인 로고" />
      </StyledImgWrapper>
      <StyledInfoTextWrapper>
        <StyledSubText>
          계정에 로그인하여 피트니스 멘토링을 시작하세요
        </StyledSubText>
      </StyledInfoTextWrapper>
    </StyledContainer>
  );
}

export default LoginIntro;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3.2rem;

  padding-top: 3.2rem;
  padding-bottom: 1.8rem;
`;

const StyledImgWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  width: 6.4rem;
  height: 6.4rem;
  border-radius: 1.6rem;
  box-shadow: 0 4px 12px rgb(0 120 111 / 20%);
`;

const StyledImg = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

const StyledInfoTextWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.1rem;
`;

const StlyedWelcome = styled.p`
  color: ${({ theme }) => theme.FONT.B01};

  ${({ theme }) => theme.TYPOGRAPHY.LB2_R};
`;

const StyledSubText = styled.p`
  color: ${({ theme }) => theme.FONT.B04};

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
`;
