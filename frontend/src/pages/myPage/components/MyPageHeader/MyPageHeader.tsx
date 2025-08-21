import styled from '@emotion/styled';
import { Link } from 'react-router-dom';

import logo from '../../../../common/assets/images/logo.svg';
import Header from '../../../../common/components/Header/Header';
import { PAGE_URL } from '../../../../common/constants/url';
import MenuDropDown from '../MenuDropDown/MenuDropDown';

function MyPageHeader() {
  return (
    <Header>
      <StyledWrapper>
        <StyledLogoLink to={PAGE_URL.HOME}>
          <StyledImg src={logo} alt="홈으로 돌아가기" />
        </StyledLogoLink>
        <StyledTitle>마이 페이지</StyledTitle>

        <MenuDropDown />
      </StyledWrapper>
    </Header>
  );
}

export default MyPageHeader;

const StyledWrapper = styled.div`
  display: flex;
  align-items: center;

  height: 100%;
  padding: 1.4rem 1.1rem;
`;

const StyledLogoLink = styled(Link)`
  display: flex;

  height: auto;
  padding: 0;
  border: none;
  border-bottom: 1px solid #e2e8f0;
  border-radius: 30%;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 10%);

  background: none;
  cursor: pointer;
`;

const StyledImg = styled.img`
  width: 3.5rem;
  aspect-ratio: 1 / 1;
`;

const StyledTitle = styled.h1`
  flex-grow: 1;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;
