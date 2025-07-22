import styled from '@emotion/styled';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import Header from '../../../../common/components/Header/Header';
function DetailHeader() {
  return (
    <Header>
      <StyledHeaderWrapper>
        <StyledBackButton>
          <StyledImg src={backIcon} alt="뒤로가기 버튼" />
        </StyledBackButton>
        <StyledTitle>상세 정보</StyledTitle>
      </StyledHeaderWrapper>
    </Header>
  );
}

export default DetailHeader;

const StyledHeaderWrapper = styled.div`
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

const StyledTitle = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
  color: ${({ theme }) => theme.FONT.B01};
`;
