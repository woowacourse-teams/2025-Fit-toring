import styled from '@emotion/styled';

import BookingForm from './components/BookingForm/BookingForm';
import MentoInfoCard from './components/MentorInfoCard/MentorInfoCard';

function Booking() {
  return (
    <StyledContainer>
      <MentoInfoCard />
      <BookingForm />
    </StyledContainer>
  );
}

export default Booking;

const StyledContainer = styled.div`
  display: flex;
  padding: 2.8rem 1.4rem;
  flex-direction: column;
  gap: 2.8rem;
`;
