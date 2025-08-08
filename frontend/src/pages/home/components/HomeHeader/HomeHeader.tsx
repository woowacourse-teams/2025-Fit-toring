import styled from '@emotion/styled';
import { Link, useNavigate } from 'react-router-dom';

import logo from '../../../../common/assets/images/logo.svg';
import { useAuth } from '../../../../common/components/AuthProvider/AuthProvider';
import Button from '../../../../common/components/Button/Button';
import Header from '../../../../common/components/Header/Header';
import { PAGE_URL } from '../../../../common/constants/url';
import MenuDropDown from '../../../myPage/components/MenuDropDown/MenuDropDown';

function HomeHeader() {
  const { authenticated } = useAuth();

  const navigate = useNavigate();

  const handleLoginClick = () => {
    navigate(PAGE_URL.LOGIN);
  };

  return (
    <Header>
      <StyledHeaderWrapper>
        <StyledTitleIconWrapper>
          <StyledLogoLink to={PAGE_URL.HOME}>
            <StyledImg src={logo} alt="홈으로 돌아가기" />
          </StyledLogoLink>
          <StyledTitle>핏토링</StyledTitle>
        </StyledTitleIconWrapper>
        {authenticated ? (
          <MenuDropDown />
        ) : (
          <Button onClick={handleLoginClick}>로그인</Button>
        )}
      </StyledHeaderWrapper>
    </Header>
  );
}

export default HomeHeader;

const StyledHeaderWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 1.05rem;

  height: 100%;
  padding: 1.4rem 1.1rem;
  justify-content: space-between;
`;

const StyledTitleIconWrapper = styled.div`
  display: flex;
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
