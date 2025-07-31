import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import menuIcon from '../../../../common/assets/images/menuBar.svg';
import Header from '../../../../common/components/Header/Header';

function MyPageHeader() {
  const navigate = useNavigate();

  const handleBackButtonClick = () => {
    navigate(-1);
  };

  return (
    <Header>
      <StyledWrapper>
        <StyledBackButton onClick={handleBackButtonClick} type="button">
          <StyledBackIcon src={backIcon} alt="뒤로가기 아이콘" />
        </StyledBackButton>
        <StyledTitle>마이 페이지</StyledTitle>
        <StyledMenuButton>
          <StyledMenuIcon src={menuIcon} alt="메뉴 열기 아이콘" />
        </StyledMenuButton>
      </StyledWrapper>
    </Header>
  );
}

export default MyPageHeader;

const StyledWrapper = styled.div`
  display: flex;
  align-items: center;

  height: 100%;
`;

const StyledBackButton = styled.button`
  margin-left: 1rem;
  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const StyledBackIcon = styled.img`
  width: 3.4rem;
`;

const StyledTitle = styled.h1`
  flex-grow: 1;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;

const StyledMenuButton = styled.button`
  margin-right: 2rem;
  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const StyledMenuIcon = styled.img`
  width: 2.4rem;
`;
