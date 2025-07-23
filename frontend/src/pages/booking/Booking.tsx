import { useState } from 'react';

import styled from '@emotion/styled';

import BookingForm from './components/BookingForm/BookingForm';
import BookingHeader from './components/BookingHeader/BookingHeader';
import CompleteModal from './components/CompleteModal/CompleteModal';
import MentoInfoCard from './components/MentorInfoCard/MentorInfoCard';

function Booking() {
  const [opened, setOpened] = useState(false);

  const handleCloseClick = () => {
    setOpened(false);
  };

  return (
    <>
      <BookingHeader />
      <StyledContentWrapper>
        <MentoInfoCard />
        <BookingForm />
      </StyledContentWrapper>
      <CompleteModal opened={opened} onCloseClick={handleCloseClick} />

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
