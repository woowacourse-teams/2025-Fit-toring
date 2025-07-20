import styled from '@emotion/styled';
import { Link } from 'react-router-dom';

import logo from '../../../../common/assets/imgs/logo.svg';
import Header from '../../../../common/components/Header/Header';

function HomeHeader() {
  return (
    <Header>
      <StyledHeaderWrapper>
        <StyledLogoLink to="/">
          <StyledImg src={logo} alt="홈으로 돌아가기" />
        </StyledLogoLink>
      </StyledHeaderWrapper>
    </Header>
  );
}

export default HomeHeader;

const StyledHeaderWrapper = styled.div`
  display: flex;
  height: 100%;
  padding: 1.4rem 1.1rem;
  align-items: center;
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
