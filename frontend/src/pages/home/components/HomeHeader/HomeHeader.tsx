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
      <S_HeaderWrapper>
        <S_TitleIconWrapper>
          <S_LogoLink to={PAGE_URL.HOME} reloadDocument>
            <S_ColorTitle>Fit</S_ColorTitle>
            <S_Title>toring</S_Title>
          </S_LogoLink>
        </S_TitleIconWrapper>
        {authenticated ? (
          <MenuDropDown />
        ) : (
          <Button onClick={handleLoginClick} variant="newPrimary">
            로그인
          </Button>
        )}
      </S_HeaderWrapper>
    </Header>
  );
}

export default HomeHeader;

const S_HeaderWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.05rem;

  height: 100%;
  padding: 1.4rem 1.1rem;
`;

const S_TitleIconWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 1.05rem;
`;

const S_LogoLink = styled(Link)`
  display: flex;
  text-decoration: none;

  height: auto;
  padding: 0;

  background: none;
  cursor: pointer;
`;

const S_ColorTitle = styled.h1`
  ${({ theme }) => theme.TYPOGRAPHY.H1_B}
  color: ${({ theme }) => theme.SYSTEM.MAIN500};
`;

const S_Title = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H1_B}
  color: ${({ theme }) => theme.SYSTEM.GRAY900};
`;
