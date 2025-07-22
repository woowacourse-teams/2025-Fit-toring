import styled from '@emotion/styled';

import DetailHeader from './components/DetailHeader/DetailHeader';
import Introduction from './components/Introduction/Introduction';
import MentorSummary from './components/MentorSummary/MentorSummary';
import Profile from './components/Profile/Profile';

function Detail() {
  return (
    <StyledContainer>
      <DetailHeader />
      <Profile />
      <MentorSummary />
      <Introduction />
    </StyledContainer>
  );
}

export default Detail;

const StyledContainer = styled.div`
  display: flex;
  padding: 0 4.1rem 2rem;
  flex-direction: column;
  align-items: center;
`;
