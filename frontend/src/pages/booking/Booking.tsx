import styled from '@emotion/styled';

import BookingForm from './components/BookingForm/BookingForm';
import BookingHeader from './components/BookingHeader/BookingHeader';
import MentoInfoCard from './components/MentorInfoCard/MentorInfoCard';

function Booking() {
  return (
    <>
      <BookingHeader />
      <StyledContentWrapper>
        <MentoInfoCard />
        <BookingForm />
      </StyledContentWrapper>
    </>
  );
}

export default Booking;

const StyledContentWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2.8rem;
  padding: 2.8rem 1.4rem;
`;
