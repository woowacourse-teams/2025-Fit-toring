import { useState } from 'react';

import styled from '@emotion/styled';

import BookingForm from './components/BookingForm/BookingForm';
import BookingHeader from './components/BookingHeader/BookingHeader';
import CompleteModal from './components/CompleteModal/CompleteModal';
import MentoInfoCard from './components/MentorInfoCard/MentorInfoCard';

import type { BookingResponse } from './types/BookingResponse';

function Booking() {
  const [opened, setOpened] = useState(false);
  const [bookedInfo, setBookedInfo] = useState<BookingResponse | null>(null);

  const handleBookingButtonClick = (bookingResponse: BookingResponse) => {
    setBookedInfo(bookingResponse);
    setOpened(true);
  };

  const handleCloseClick = () => {
    setOpened(false);
  };

  return (
    <>
      <BookingHeader />
      <StyledContentWrapper>
        <MentoInfoCard />
        <BookingForm handleBookingButtonClick={handleBookingButtonClick} />
      </StyledContentWrapper>
      <CompleteModal
        bookedInfo={bookedInfo}
        opened={opened}
        onCloseClick={handleCloseClick}
      />
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
