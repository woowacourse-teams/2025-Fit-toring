import React from 'react';

import styled from '@emotion/styled';
import { Link } from 'react-router-dom';

function Footer() {
  return (
    <StyledContainer>
      <StyledTextWrapper>
        <StyledText>상호명: 핏토링</StyledText>
        <StyledText>대표자: 주용은</StyledText>
        <StyledText>이메일: fittoring@gmail.com</StyledText>
        <StyledText>Ⓒ 2025. fittoring Inc. All right reserved.</StyledText>
      </StyledTextWrapper>
      <StyledLink to="https://docs.google.com/forms/d/e/1FAIpQLSfQlaSrxUmU-CKnK6jnp8qLTdGMmLYbff2CZSUmKE09OHN11w/viewform">
        서비스 문의하기
      </StyledLink>
    </StyledContainer>
  );
}

export default Footer;

const StyledContainer = styled.div`
  height: 20rem;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  background: ${({ theme }) => theme.SYSTEM.GRAY50};
  gap: 1.5rem;
`;

const StyledTextWrapper = styled.div`
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  gap: 0.7rem;
`;

const StyledText = styled.p`
  font-size: 1.4rem;
`;

const StyledLink = styled(Link)`
  cursor: pointer;
  color: black;
  ${({ theme }) => theme.TYPOGRAPHY.B2_B};
`;
