import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import Header from '../../../../common/components/Header/Header';

function BookingHeader() {
  const navigate = useNavigate();

  const handleBackButtonClick = () => {
    navigate(-1);
  };

  return (
    <Header>
      <StyledWrapper>
        <StyledBackButton onClick={handleBackButtonClick}>
          <StyledBackIcon src={backIcon} alt="뒤로가기 아이콘" />
        </StyledBackButton>
        <StyledTitle>예약 신청</StyledTitle>
      </StyledWrapper>
    </Header>
  );
}

export default BookingHeader;

const StyledWrapper = styled.div`
  display: flex;
  height: 100%;
  align-items: center;
`;

const StyledBackButton = styled.button`
  position: absolute;
  margin-left: 1rem;
  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const StyledTitle = styled.h1`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}

  flex-grow: 1;

  text-align: center;

  color: ${({ theme }) => theme.FONT.B01};
`;

const StyledBackIcon = styled.img`
  width: 3.4rem;
`;
