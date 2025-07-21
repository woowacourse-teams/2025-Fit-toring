import styled from '@emotion/styled';

import backIcon from '../../../../common/assets/images/backButton.svg';
import Header from '../../../../common/components/Header/Header';

function BookingHeader() {
  return (
    <Header>
      <StyledWrapper>
        <StyledBackButton>
          <StyledBackIconImg src={backIcon} alt="뒤로가기 아이콘" />
        </StyledBackButton>
        <StyledPageName>예약 신청</StyledPageName>
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
  border: none;
  padding: 0px;
  background-color: transparent;
  cursor: pointer;
`;

const StyledPageName = styled.h1`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}

  flex-grow: 1;
  text-align: center;
`;

const StyledBackIconImg = styled.img`
  width: 3.4rem;
`;
