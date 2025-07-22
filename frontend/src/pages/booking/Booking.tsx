import { useState } from 'react';

import styled from '@emotion/styled';

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
      </StyledContentWrapper>
      <CompleteModal opened={opened} onCloseClick={handleCloseClick} />
    </>
  );
}

export default Booking;

const StyledContentWrapper = styled.div`
  padding: 1.4rem;
`;
