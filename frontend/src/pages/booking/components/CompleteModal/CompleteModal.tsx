import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import bookedIcon from '../../../../common/assets/images/bookedIcon.svg';
import Button from '../../../../common/components/Button/Button';
import Modal from '../../../../common/components/Modal/Modal';
import { PAGE_URL } from '../../../../common/constants/url';

import type { BookingResponse } from '../../types/BookingResponse';

interface CompleteModalProps {
  bookedInfo: BookingResponse | null;
  opened: boolean;
  onCloseClick: () => void;
}

function CompleteModal({
  bookedInfo,
  opened,
  onCloseClick,
}: CompleteModalProps) {
  const navigate = useNavigate();

  const handleGoHome = () => {
    onCloseClick();
    navigate(PAGE_URL.HOME);
  };

  return (
    <Modal opened={opened} onCloseClick={onCloseClick}>
      <StyledContainer>
        <StyledCompleteNotice>
          <StyledImg src={bookedIcon} alt="Booked Icon" />
          <h3>신청이 완료되었습니다!</h3>
          <div>
            <p>
              <strong>{bookedInfo?.mentorName}</strong> 멘토에게
            </p>
            <p>상담 신청이 전송되었습니다.</p>
          </div>
        </StyledCompleteNotice>
        <StyledInfoWrapper>
          <p>신청자: {bookedInfo?.menteeName}</p>
          <p>연락처: {bookedInfo?.menteePhone}</p>
          <p>멘토 연락처: {bookedInfo?.mentorPhone}</p>
        </StyledInfoWrapper>
        <StyledCompleteGuide>
          <div>
            <p>멘토가 확인 후 연락드릴 예정입니다.</p>
            <p>조금만 기다려주세요!</p>
          </div>
        </StyledCompleteGuide>
        <Button
          onClick={handleGoHome}
          size="full"
          customStyle={css`
            padding: 0.8rem;

            font-size: 1.2rem;
          `}
        >
          홈으로 돌아가기
        </Button>
      </StyledContainer>
    </Modal>
  );
}
export default CompleteModal;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;

  height: 43.5rem;
  padding: 4rem 2.2rem 2.4rem;
`;

const StyledCompleteNotice = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;

  gap: 1.6rem;

  & p {
    ${({ theme }) => theme.TYPOGRAPHY.B2_B};
    color: ${({ theme }) => theme.FONT.B03};
    line-height: 2.2rem;
    text-align: center;
  }

  & strong {
    ${({ theme }) => theme.TYPOGRAPHY.B2_B};
    color: ${({ theme }) => theme.SYSTEM.MAIN600};
  }

  & h3 {
    ${({ theme }) => theme.TYPOGRAPHY.H3_R};
    color: ${({ theme }) => theme.FONT.B01};
  }
`;

const StyledImg = styled.img`
  width: 5.6rem;
  height: 5.6rem;
`;

const StyledInfoWrapper = styled.div`
  display: flex;

  width: 100%;
  padding: 1.5rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};

  background-color: ${({ theme }) => theme.BG.LIGHT};
  flex-direction: column;
  gap: 0.8rem;
  border-radius: 0.8rem;

  & p {
    ${({ theme }) => theme.TYPOGRAPHY.B2_R};
    color: ${({ theme }) => theme.FONT.B03};
  }
`;

const StyledCompleteGuide = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;

  gap: 1.6rem;

  & p {
    ${({ theme }) => theme.TYPOGRAPHY.B4_R};
    color: ${({ theme }) => theme.FONT.B04};
    line-height: 1.9rem;
    text-align: center;
  }
`;
