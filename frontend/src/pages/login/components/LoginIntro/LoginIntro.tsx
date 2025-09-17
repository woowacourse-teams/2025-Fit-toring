import styled from '@emotion/styled';

import logo from '../../../../common/assets/images/logo.svg';

function LoginIntro() {
  return (
    <S_Container>
      <S_ImgWrapper>
        <S_Img src={logo} alt="핏토링 메인 로고" />
      </S_ImgWrapper>
      <S_InfoTextWrapper>
        <S_SubText>계정에 로그인하여 피트니스 멘토링을 시작하세요</S_SubText>
      </S_InfoTextWrapper>
    </S_Container>
  );
}

export default LoginIntro;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3.2rem;

  padding-top: 3.2rem;
  padding-bottom: 1.8rem;
`;

const S_ImgWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  width: 6.4rem;
  height: 6.4rem;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgb(0 120 111 / 20%);
`;

const S_Img = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

const S_InfoTextWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.1rem;
`;

const S_SubText = styled.p`
  color: ${({ theme }) => theme.FONT.B04};

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
`;
