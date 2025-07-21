import styled from '@emotion/styled';

import BookingHeader from './components/BookingHeader/BookingHeader';
import MentoInfoCard from './components/MentorInfoCard/MentorInfoCard';

function Booking() {
  return (
    <StyledContainer>
      <BookingHeader />
      <StyledContentWrapper>
        <MentoInfoCard />
      </StyledContentWrapper>
    </StyledContainer>
  );
}

export default Booking;

const StyledContainer = styled.div``;

const StyledContentWrapper = styled.div`
  padding: 1.4rem;
`;
