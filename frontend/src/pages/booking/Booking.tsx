import styled from '@emotion/styled';

import MentoInfoCard from './components/MentorInfoCard/MentorInfoCard';

function Booking() {
  return (
    <StyledContainer>
      <MentoInfoCard />
    </StyledContainer>
  );
}

export default Booking;

const StyledContainer = styled.div`
  padding: 1.4rem;
`;
