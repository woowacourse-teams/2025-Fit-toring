import styled from '@emotion/styled';

import BookingForm from './components/BookingForm/BookingForm';
import MentoInfoCard from './components/MentorInfoCard/MentorInfoCard';

function Booking() {
  return (
    <StyledContainer>
      <BookingForm />
      <MentoInfoCard />
    </StyledContainer>
  );
}

export default Booking;

const StyledContainer = styled.div`
  padding: 1.4rem;
`;
