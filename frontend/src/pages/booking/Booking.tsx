import { useRef, useState, useLayoutEffect } from 'react';

import styled from '@emotion/styled';

import BookingForm from './components/BookingForm/BookingForm';
import BookingHeader from './components/BookingHeader/BookingHeader';
import CompleteModal from './components/CompleteModal/CompleteModal';
import MentoInfoCard from './components/MentorInfoCard/MentorInfoCard';
import { smoothScrollTo } from './utils/smoothScrollTo';

function Booking() {
  const [opened, setOpened] = useState(false);

  const handleCloseClick = () => {
    setOpened(false);
  };

  const containerRef = useRef<HTMLDivElement | null>(null);
  const wrapperRef = useRef<HTMLDivElement | null>(null);
  const formRef = useRef<HTMLDivElement | null>(null);

  useLayoutEffect(() => {
    if (containerRef.current && formRef.current && wrapperRef.current) {
      const { top: formTop } = formRef.current.getBoundingClientRect();
      const { top: wrapperTop } = wrapperRef.current.getBoundingClientRect();

      const style = window.getComputedStyle(wrapperRef.current);
      const paddingTop = parseFloat(style.paddingTop);

      const absoluteFormTop = formTop + window.scrollY;
      const absoluteWrapperTop = wrapperTop + window.scrollY;

      containerRef.current.style.height = `calc(100vh + ${absoluteFormTop - absoluteWrapperTop - paddingTop}px)`;

      smoothScrollTo(absoluteFormTop, 1000);
    }
  }, []);

  return (
    <div ref={containerRef}>
      <BookingHeader />
      <StyledContentWrapper ref={wrapperRef}>
        <MentoInfoCard />
        <div ref={formRef}>
          <BookingForm />
        </div>
      </StyledContentWrapper>

      <CompleteModal opened={opened} onCloseClick={handleCloseClick} />
    </div>
  );
}

export default Booking;

const StyledContentWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2.8rem;
  padding: 2.8rem 1.4rem;
`;
