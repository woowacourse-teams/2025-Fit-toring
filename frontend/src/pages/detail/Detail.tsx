import styled from '@emotion/styled';

import DetailHeader from './components/DetailHeader/DetailHeader';
import Introduction from './components/Introduction/Introduction';
import Profile from './components/Profile/Profile';
import Summary from './components/Summary/Summary';

function Detail() {
  return (
    <StyledContainer>
      <DetailHeader />
      <Profile />
      <Summary />
      <Introduction />
    </StyledContainer>
  );
}

export default Detail;

const StyledContainer = styled.div`
  display: flex;
  padding: 0 4.1rem;
  flex-direction: column;
  align-items: center;
`;
