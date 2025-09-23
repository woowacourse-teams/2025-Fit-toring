import styled from '@emotion/styled';
import { Link } from 'react-router-dom';

function Footer() {
  return (
    <S_Container>
      <S_TextWrapper>
        <S_Text>상호명: 핏토링</S_Text>
        <S_Text>대표자: 주용은</S_Text>
        <S_Text>이메일: fittoring7@gmail.com</S_Text>
        <S_Text>Ⓒ 2025. fittoring Inc. All right reserved.</S_Text>
      </S_TextWrapper>
      <S_Link
        to="https://docs.google.com/forms/d/e/1FAIpQLSfQlaSrxUmU-CKnK6jnp8qLTdGMmLYbff2CZSUmKE09OHN11w/viewform"
        target="_blank"
      >
        서비스 문의하기
      </S_Link>
    </S_Container>
  );
}

export default Footer;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.5rem;

  height: 20rem;

  background: ${({ theme }) => theme.SYSTEM.GRAY50};
`;

const S_TextWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.7rem;
`;

const S_Text = styled.p`
  font-size: 1.4rem;
`;

const S_Link = styled(Link)`
  cursor: pointer;

  color: black;
  ${({ theme }) => theme.TYPOGRAPHY.B2_B};
`;
