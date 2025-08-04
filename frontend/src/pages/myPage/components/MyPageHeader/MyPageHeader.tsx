import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import Header from '../../../../common/components/Header/Header';
import MenuDropDown from '../MenuDropDown/MenuDropDown';

function MyPageHeader() {
  const navigate = useNavigate();

  const handleBackButtonClick = () => {
    navigate(-1);
  };

  return (
    <Header>
      <StyledWrapper>
        <StyledButtonWrapper>
          <StyledBackButton onClick={handleBackButtonClick} type="button">
            <StyledBackIcon src={backIcon} alt="뒤로가기 아이콘" />
          </StyledBackButton>
        </StyledButtonWrapper>
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
`;

const StyledButtonWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 5rem;
`;

const StyledBackButton = styled.button`
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
