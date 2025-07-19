import styled from '@emotion/styled';

import backButton from '../../../common/assets/images/backButton.svg';
import Header from '../../../common/components/Header/Header';

function DetailHeader() {
  return (
    <Header>
      <StyledHeaderInner>
        <StyledBackButton>
          <StyledImg src={backButton}></StyledImg>
        </StyledBackButton>
        <Title>예약 신청</Title>
      </StyledHeaderInner>
    </Header>
  );
}

export default DetailHeader;

const StyledHeaderInner = styled.div`
  display: flex;
  position: relative;
  height: 100%;
  align-items: center;
  justify-content: center;
`;

const StyledBackButton = styled.button`
  position: absolute;
  left: 1.6rem;
  padding: 0;
  border: none;
  background: none;
  cursor: pointer;
`;

const StyledImg = styled.img`
  width: 5.4rem;
  height: 5.7rem;
`;

const Title = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
  color: ${({ theme }) => theme.FONT.B01};
`;
