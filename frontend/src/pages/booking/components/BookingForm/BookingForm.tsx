import { useState } from 'react';

import styled from '@emotion/styled';

import { apiClient } from '../../../../common/apis/apiClient';
import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';
import { API_ENDPOINTS } from '../../../../common/constants/apiEndpoints';
import useNameInput from '../../../../common/hooks/useNameInput';
import useFormattedPhoneNumber from '../../hooks/useFormattedPhoneNumber';
import { getPhoneNumberErrorMessage } from '../../utils/phoneNumberValidator';
import BookingSummarySection from '../BookingSummarySection/BookingSummarySection';

import type { BookingResponse } from '../../types/BookingResponse';

interface BookingFormProps {
  handleBookingButtonClick: (bookingResponse: BookingResponse) => void;
  mentoringId: number;
}

function BookingForm({
  handleBookingButtonClick,
  mentoringId,
}: BookingFormProps) {
  const {
    name,
    handleNameChange,
    errorMessage: menteeNameErrorMessage,
  } = useNameInput();

  const { phoneNumber, inputRef, handlePhoneNumberChange } =
    useFormattedPhoneNumber();
  const [counselContent, setCounselContent] = useState('');

  const phoneNumberErrorMessage = getPhoneNumberErrorMessage(phoneNumber);

  const handleCounselContentChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>,
  ) => {
    setCounselContent(e.target.value);
  };

  const handleBooking = async () => {
    try {
      const response = await apiClient.post({
        endpoint: `${API_ENDPOINTS.MENTORINGS}/${mentoringId}${API_ENDPOINTS.RESERVATION}`,
        searchParams: {
          name,
          menteePhone: phoneNumber,
          content: counselContent,
        },
      });
      handleBookingButtonClick(response);
    } catch (error) {
      console.error('예약 중 에러 발생', error);
    }
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    handleBooking();
  };

  return (
    <StyledContainer onSubmit={handleSubmit}>
      <StyledInfoText>
        아래 정보를 입력해주시면 멘토에게 상담 신청이 전송됩니다.
      </StyledInfoText>
      <StyledFieldWrapper>
        <FormField label="상담자명 *" errorMessage={menteeNameErrorMessage}>
          <Input
            placeholder="홍길동"
            id="name"
            value={name}
            onChange={handleNameChange}
            errored={menteeNameErrorMessage !== ''}
            data-testid="mentee-name-input"
            required
          />
        </FormField>
        <FormField label="전화번호 *" errorMessage={phoneNumberErrorMessage}>
          <Input
            placeholder="010-1234-4986"
            id="phone"
            value={phoneNumber}
            onChange={handlePhoneNumberChange}
            errored={phoneNumberErrorMessage !== ''}
            ref={inputRef}
            maxLength={13}
            type="tel"
            data-testid="phone-number-input"
            required
          />
        </FormField>
        <FormField label="상담 내용(선택사항)" errorMessage={''}>
          <StyledTextarea
            id="details"
            placeholder="구체적으로 궁금한 내용이나 현재 상황을 적어주시면 
더 정확한 조언을 받을 수 있습니다."
            onChange={handleCounselContentChange}
            errored={false}
            value={counselContent}
          />
        </FormField>
      </StyledFieldWrapper>
      <BookingSummarySection />
    </StyledContainer>
  );
}

export default BookingForm;

const StyledContainer = styled.form`
  width: 100%;
  height: 100%;
  padding: 2.2rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 1.3rem;

  background-color: white;
`;

const StyledInfoText = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  margin-top: 1.7rem;

  color: ${({ theme }) => theme.FONT.B03};
`;

const StyledFieldWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2.1rem;

  margin-top: 3.3rem;
  margin-bottom: 3.2rem;
`;

const StyledTextarea = styled.textarea<{ errored: boolean }>`
  width: 100%;
  height: 5.8rem;
  padding: 0.7rem 1.1rem;
  border: ${({ theme, errored }) =>
      errored ? theme.FONT.ERROR : theme.OUTLINE.DARK}
    1px solid;
  border-radius: 0.7rem;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  resize: none;

  :focus {
    outline: none;
  }

  color: ${({ theme }) => theme.FONT.B04};
`;
