import styled from '@emotion/styled';

import DetailHeader from './components/DetailHeader/DetailHeader';
import Introduction from './components/Introduction/Introduction';
import Profile from './components/Profile/Profile';
import Summary from './components/Summary/Summary';

function Detail() {
  return (
    <>
      <StyledContainer>
        <DetailHeader />
        <Profile />
        <Summary />
        <StyledH4>멘토 소개</StyledH4>
        <Introduction />
      </StyledContainer>
    </>
  );
}

export default Detail;

const StyledContainer = styled.div`
  display: flex;
  padding: 0 4.1rem;
  flex-direction: column;
  align-items: center;
`;

const StyledH4 = styled.h4`
  display: flex;
  justify-content: center;
  margin-bottom: 1.7rem;

  ${({ theme }) => theme.TYPOGRAPHY.H4_R}
  color: ${({ theme }) => theme.FONT.B01};
`;
