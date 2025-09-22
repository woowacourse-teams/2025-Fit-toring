import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import { apiClient } from '../../../../common/apis/apiClient';
import { getUserInfo } from '../../../../common/apis/getUserInfo';
import FormField from '../../../../common/components/FormField/FormField';
import { API_ENDPOINTS } from '../../../../common/constants/apiEndpoints';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import BookingSummarySection from '../BookingSummarySection/BookingSummarySection';
import Checkbox from '../Checkbox/Checkbox';
import { validateTextarea } from '../../../../common/utils/validateDetail';

interface BookingFormProps {
  handleBookingButtonClick: () => void;
  mentoringId: number;
  mentoringPrice: number;
}

function BookingForm({
  handleBookingButtonClick,
  mentoringId,
  mentoringPrice,
}: BookingFormProps) {
  const [counselContent, setCounselContent] = useState('');
  const [userInfo, setUserInfo] = useState({
    name: '',
    phoneNumber: '',
  });

  const [errored, setErrored] = useState({
    textarea: false,
  });

  const detailErrorMessage = validateTextarea(counselContent);

  const handleCounselContentChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>,
  ) => {
    setCounselContent(e.target.value);
    !errored.textarea && e.target.value.length > 5000
      ? setErrored((prev) => ({
          ...prev,
          textarea: true,
        }))
      : setErrored((prev) => ({
          ...prev,
          textarea: false,
        }));
  };

  const handleBooking = async () => {
    try {
      await apiClient.post({
        endpoint: `${API_ENDPOINTS.MENTORINGS}/${mentoringId}${API_ENDPOINTS.RESERVATION}`,
        body: {
          content: counselContent,
        },
        withCredentials: true,
      });

      handleBookingButtonClick();
    } catch (error) {
      console.error('예약 중 에러 발생', error);

      captureSentryError({
        error,
        level: 'warning',
        feature: 'reservation',
        step: 'reservation-apply',
      });
    }
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!!detailErrorMessage) return;

    handleBooking();
  };

  useEffect(() => {
    const fetchUserInfo = async () => {
      const { name, phoneNumber } = await getUserInfo();

      setUserInfo({ name, phoneNumber });
    };

    fetchUserInfo();
  }, []);

  return (
    <S_Container onSubmit={handleSubmit}>
      <S_InfoText>
        아래 정보를 입력해주시면 멘토에게 상담 신청이 전송됩니다.
      </S_InfoText>
      <S_UserInfoWrapper>
        <S_InfoRow>
          <S_UserInfoLabel>상담자명</S_UserInfoLabel>
          <S_UserInfoText>{userInfo.name}</S_UserInfoText>
        </S_InfoRow>
        <FormField
          label="상담 내용(선택사항)"
          errorMessage={detailErrorMessage}
        >
          <S_Textarea
            id="details"
            placeholder="구체적으로 궁금한 내용이나 현재 상황을 적어주시면 
더 정확한 조언을 받을 수 있습니다."
            onChange={handleCounselContentChange}
            errored={errored.textarea}
            value={counselContent}
          />
        </FormField>
      </S_UserInfoWrapper>

      <BookingSummarySection price={mentoringPrice} />
    </S_Container>
  );
}

export default BookingForm;

const S_Container = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1.7rem;

  width: 100%;
  height: 100%;
  padding: 2.2rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 1.3rem;

  background-color: white;
`;

const S_InfoText = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  color: ${({ theme }) => theme.FONT.B03};
`;

const S_UserInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.7rem;
`;

const S_InfoRow = styled.div`
  display: flex;
  flex-direction: column;
`;

const S_UserInfoLabel = styled.p`
  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
`;

const S_UserInfoText = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  color: ${({ theme }) => theme.FONT.B01};
`;

const S_Textarea = styled.textarea<{ errored: boolean }>`
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
