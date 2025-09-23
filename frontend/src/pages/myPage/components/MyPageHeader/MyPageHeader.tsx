import styled from '@emotion/styled';
import { Link } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import Header from '../../../../common/components/Header/Header';
import { PAGE_URL } from '../../../../common/constants/url';
import MenuDropDown from '../MenuDropDown/MenuDropDown';

function MyPageHeader() {
  return (
    <Header>
      <S_Wrapper>
        <S_LogoLink to={PAGE_URL.HOME}>
          <S_Img src={backIcon} alt="홈으로 돌아가기" />
        </S_LogoLink>
        <S_Title>마이 페이지</S_Title>

        <MenuDropDown />
      </S_Wrapper>
    </Header>
  );
}

export default MyPageHeader;

const S_Wrapper = styled.div`
  display: flex;
  align-items: center;

  height: 100%;
  padding: 1.4rem 1.1rem;
`;

const S_LogoLink = styled(Link)`
  display: flex;

  height: auto;

  background: none;
  cursor: pointer;
`;

const S_Img = styled.img`
  width: 3.5rem;
  height: 3.5rem;
  aspect-ratio: 1 / 1;
`;

const S_Title = styled.h1`
  flex-grow: 1;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;
