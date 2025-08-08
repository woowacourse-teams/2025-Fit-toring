import { useEffect, useState } from 'react';

import styled from '@emotion/styled';
import * as Sentry from '@sentry/react';

import { apiClient } from '../../../../common/apis/apiClient';
import { getUserInfo } from '../../../../common/apis/getUserInfo';
import FormField from '../../../../common/components/FormField/FormField';
import { API_ENDPOINTS } from '../../../../common/constants/apiEndpoints';
import BookingSummarySection from '../BookingSummarySection/BookingSummarySection';
import Checkbox from '../Checkbox/Checkbox';

import type { BookingResponse } from '../../types/BookingResponse';

interface BookingFormProps {
  handleBookingButtonClick: (bookingResponse: BookingResponse) => void;
  mentoringId: number;
}

function BookingForm({
  handleBookingButtonClick,
  mentoringId,
}: BookingFormProps) {
  const [counselContent, setCounselContent] = useState('');
  const [userInfo, setUserInfo] = useState({
    name: '',
    phoneNumber: '',
  });
  const [sharingAgreed, setSharingAgreed] = useState(false);

  const [errored, setErrored] = useState(false);

  const handleCounselContentChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>,
  ) => {
    setCounselContent(e.target.value);
  };

  const handleBooking = async () => {
    try {
      const response = await apiClient.post({
        endpoint: `${API_ENDPOINTS.MENTORINGS}/${mentoringId}${API_ENDPOINTS.RESERVATION}`,
        body: {
          content: counselContent,
        },
        withCredentials: true,
      });
      const data = await response.json();

      handleBookingButtonClick(data);
    } catch (error) {
      console.error('예약 중 에러 발생', error);

      Sentry.captureException(error, {
        level: 'warning',
        tags: {
          feature: 'reservation',
          step: 'reservation-apply',
        },
      });
    }
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!sharingAgreed) {
      setErrored(true);
      return;
    }

    handleBooking();
  };

  const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setErrored(false);
    setSharingAgreed(e.target.checked);
  };

  useEffect(() => {
    const fetchUserInfo = async () => {
      const { name, phoneNumber } = await getUserInfo();

      setUserInfo({ name, phoneNumber });
    };

    fetchUserInfo();
  }, []);

  return (
    <StyledContainer onSubmit={handleSubmit}>
      <StyledInfoText>
        아래 정보를 입력해주시면 멘토에게 상담 신청이 전송됩니다.
      </StyledInfoText>
      <StyledUserInfoWrapper>
        <StyledInfoRow>
          <StyledUserInfoLabel>상담자명</StyledUserInfoLabel>
          <StyledUserInfoText>{userInfo.name}</StyledUserInfoText>
        </StyledInfoRow>
        <StyledInfoRow>
          <StyledUserInfoLabel>전화번호</StyledUserInfoLabel>
          <StyledUserInfoText>{userInfo.phoneNumber}</StyledUserInfoText>
        </StyledInfoRow>
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
      </StyledUserInfoWrapper>
      <StyledLabelWrapper>
        <Checkbox
          id="sharingAgreed"
          checked={sharingAgreed}
          onChange={handleCheckboxChange}
          errored={errored}
          label={
            <StyledCheckboxLabelText>
              전화번호 제공 동의
            </StyledCheckboxLabelText>
          }
        />
        <StyledCheckboxSubText>
          멘토 승인이 완료되면, 상담을 위해 내 전화번호가 멘토에게 전달됩니다.
        </StyledCheckboxSubText>
        {errored && (
          <StyledErrorText>전화번호 제공 동의를 해주세요.</StyledErrorText>
        )}
      </StyledLabelWrapper>

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

const StyledUserInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2.1rem;

  margin-top: 3.3rem;
`;

const StyledInfoRow = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const StyledUserInfoLabel = styled.p`
  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
`;

const StyledUserInfoText = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  color: ${({ theme }) => theme.FONT.B01};
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

  color: ${({ theme }) => theme.FONT.B01};
`;

const StyledLabelWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.6rem;

  margin: 2rem 0;
`;

const StyledCheckboxLabelText = styled.strong`
  color: ${({ theme }) => theme.FONT.B03};
  font-weight: bold;
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
`;

const StyledCheckboxSubText = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  color: ${({ theme }) => theme.FONT.B03};
`;

const StyledErrorText = styled.span`
  color: ${({ theme }) => theme.FONT.ERROR};
  ${({ theme }) => theme.TYPOGRAPHY.C3_R};
`;
