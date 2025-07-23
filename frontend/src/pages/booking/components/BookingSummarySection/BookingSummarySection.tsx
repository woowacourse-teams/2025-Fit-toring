import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { apiClient } from '../../../../common/apis/apiClient';
import timeIcon from '../../../../common/assets/images/timeIcon.svg';
import Button from '../../../../common/components/Button/Button';
import TextWithIcon from '../../../../common/components/TextWithIcon/TextWithIcon';
import { API_URL } from '../../../../common/constants/url';

import type { BookingResponse } from '../../types/BookingResponse';

interface BookingSummarySectionProps {
  onBookingSuccess: (bookingResponse: BookingResponse) => void;
  menteeName: string;
  phoneNumber: string;
  counselContent: string;
}

function BookingSummarySection({
  onBookingSuccess,
  menteeName,
  phoneNumber,
  counselContent,
}: BookingSummarySectionProps) {
  const handleClick = async () => {
    try {
      const response = await apiClient.post({
        endpoint: `${API_URL.MENTORINGS}/1/reservation`,
        searchParams: {
          menteeName,
          menteePhone: phoneNumber,
          content: counselContent,
        },
      });
      onBookingSuccess(response);
    } catch (error) {
      console.error('예약 중 에러 발생', error);
    }
  };

  return (
    <StyledContainer>
      <StyledWrapper>
        <TextWithIcon iconSrc={timeIcon} iconName="시간 아이콘" text="15분" />
        <StyledPrice>5,000원</StyledPrice>
      </StyledWrapper>
      <Button
        customStyle={css`
          flex-grow: 1;
          padding: 0.8rem 0;
        `}
        onClick={handleClick}
      >
        예약하기
      </Button>
    </StyledContainer>
  );
}

export default BookingSummarySection;

const StyledContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
  width: 100%;
  height: 100%;
`;

const StyledWrapper = styled.div`
  display: flex;
  flex-direction: column;
  padding-left: 1.1rem;
  gap: 0.22rem;
`;

const StyledPrice = styled.span`
  color: ${({ theme }) => theme.SYSTEM.MAIN600};
  ${({ theme }) => theme.TYPOGRAPHY.B1_B};
  font-weight: 600;
`;
