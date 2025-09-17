import React from 'react';

import styled from '@emotion/styled';

import logo from '../../../../common/assets/images/logo.svg';

function SignupIntro() {
  return (
    <S_Container>
      <S_ImgWrapper>
        <S_Img src={logo} alt="핏토링 메인 로고" />
      </S_ImgWrapper>
      <S_InfoTextWrapper>
        <StlyedWelcome>핏토링에 오신 것을 환영합니다!</StlyedWelcome>
        <S_SubText>계정을 만들고 전문 피트니스 멘토링을 시작하세요</S_SubText>
      </S_InfoTextWrapper>
    </S_Container>
  );
}

export default SignupIntro;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3.2rem;

  padding-top: 3.2rem;
  padding-bottom: 1.8rem;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.LIGHT};

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_ImgWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  width: 6.4rem;
  height: 6.4rem;
  border-radius: 1.6rem;
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

const StlyedWelcome = styled.p`
  color: ${({ theme }) => theme.FONT.B01};

  ${({ theme }) => theme.TYPOGRAPHY.LB2_R};
`;

const S_SubText = styled.p`
  color: ${({ theme }) => theme.FONT.B04};

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
`;
