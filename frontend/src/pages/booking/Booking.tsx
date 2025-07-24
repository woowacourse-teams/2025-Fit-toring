import { useRef, useState, useLayoutEffect, useEffect } from 'react';

import styled from '@emotion/styled';
import { useParams } from 'react-router-dom';

import {
  getMentoringDetail,
  type MentoringDetail,
} from './apis/getMentoringDetail';
import BookingForm from './components/BookingForm/BookingForm';
import BookingHeader from './components/BookingHeader/BookingHeader';
import CompleteModal from './components/CompleteModal/CompleteModal';
import MentoInfoCard from './components/MentorInfoCard/MentorInfoCard';
import { smoothScrollTo } from './utils/smoothScrollTo';

function Booking() {
  const [opened, setOpened] = useState(false);
  const [mentorDetail, setMentorDetail] = useState<MentoringDetail | null>(
    null,
  );

  const { mentoringId } = useParams();

  const handleCloseClick = () => {
    setOpened(false);
  };

  const containerRef = useRef<HTMLDivElement | null>(null);
  const wrapperRef = useRef<HTMLDivElement | null>(null);
  const formRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const fetchMentorDetail = async () => {
      try {
        const response = await getMentoringDetail(mentoringId!);
        setMentorDetail(response);
      } catch (error) {
        console.error('멘토링 정보 조회 실패:', error);
      }
    };

    fetchMentorDetail();
  }, [mentoringId]);

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
        <MentoInfoCard mentorDetail={mentorDetail} />
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
