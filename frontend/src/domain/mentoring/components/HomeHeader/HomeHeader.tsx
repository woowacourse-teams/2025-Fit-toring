import styled from '@emotion/styled';
import { Link } from 'react-router-dom';

import logo from '../../../../common/assets/imgs/logo.svg';
import Header from '../../../../common/components/Header/Header';
import { PAGE_URL } from '../../../common/constants/url';

function HomeHeader() {
  return (
    <StyledContainer>
      <Header>
        <StyledHeaderWrapper>
          <StyledLogoLink to={PAGE_URL.HOME}>
            <StyledImg src={logo} alt="홈으로 돌아가기" />
          </StyledLogoLink>
          <StyledTitle>핏토링</StyledTitle>
        </StyledHeaderWrapper>
      </Header>
    </StyledContainer>
  );
}

export default HomeHeader;

const StyledContainer = styled.div`
  margin-bottom: 5.7rem;
`;

const StyledHeaderWrapper = styled.div`
  display: flex;
  height: 100%;
  padding: 1.4rem 1.1rem;
  align-items: center;
  gap: 1.05rem;
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

const StyledTitle = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
  color: ${({ theme }) => theme.SYSTEM.MAIN800};
`;
