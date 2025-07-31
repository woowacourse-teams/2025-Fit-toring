import styled from '@emotion/styled';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import Header from '../../../../common/components/Header/Header';

function MyPageHeader() {
  return (
    <Header>
      <StyledWrapper>
        <StyledBackButton onClick={() => {}} type="button">
          <StyledBackIcon src={backIcon} alt="뒤로가기 아이콘" />
        </StyledBackButton>
        <StyledTitle>마이 페이지</StyledTitle>
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
  position: absolute;

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
