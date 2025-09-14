import styled from '@emotion/styled';
import { Link, useNavigate } from 'react-router-dom';

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
          <StyledLogoLink to={PAGE_URL.HOME} reloadDocument>
            <StyledColorTitle>Fit</StyledColorTitle>
            <StyledTitle>toring</StyledTitle>
          </StyledLogoLink>
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
  justify-content: space-between;
  gap: 1.05rem;

  height: 100%;
  padding: 1.4rem 1.1rem;
`;

const StyledTitleIconWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 1.05rem;
`;

const StyledLogoLink = styled(Link)`
  display: flex;
  text-decoration: none;

  height: auto;
  padding: 0;

  background: none;
  cursor: pointer;
`;

const StyledColorTitle = styled.h1`
  ${({ theme }) => theme.TYPOGRAPHY.H1_B}
  color: ${({ theme }) => theme.SYSTEM.MAIN500};
`;

const StyledTitle = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H1_B}
  color: ${({ theme }) => theme.SYSTEM.GRAY900};
`;
