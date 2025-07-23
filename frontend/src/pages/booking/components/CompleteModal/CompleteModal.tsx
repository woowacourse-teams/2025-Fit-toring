import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import bookedIcon from '../../../../common/assets/images/bookedIcon.svg';
import Button from '../../../../common/components/Button/Button';
import Modal from '../../../../common/components/Modal/Modal';
import { PAGE_URL } from '../../../../common/constants/url';

interface CompleteModalProps {
  opened: boolean;
  onCloseClick: () => void;
}

function CompleteModal({ opened, onCloseClick }: CompleteModalProps) {
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
              <strong>김트레이너</strong> 멘토에게
            </p>
            <p>상담 신청이 전송되었습니다.</p>
          </div>
        </StyledCompleteNotice>
        <StyledInfoWrapper>
          <p>신청자: 홍길동</p>
          <p>연락처: 010-1234-1234</p>
          <p>멘토 연락처: 010-5678-5678</p>
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

  width: 34rem;
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
    color: ${({ theme }) => theme.FONT.B02};
    line-height: 2.2rem;
    text-align: center;
  }

  & strong {
    ${({ theme }) => theme.TYPOGRAPHY.B2_B};
    color: ${({ theme }) => theme.SYSTEM.MAIN600};
  }

  & h3 {
    ${({ theme }) => theme.TYPOGRAPHY.H3_R};
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
  border: 1px solid ${({ theme }) => theme.LINE.REGULAR};

  background-color: ${({ theme }) => theme.BG.LIGHT};
  flex-direction: column;
  gap: 0.8rem;
  border-radius: 0.8rem;

  & p {
    ${({ theme }) => theme.TYPOGRAPHY.B2_R};
    color: ${({ theme }) => theme.FONT.B02};
  }
`;

const StyledCompleteGuide = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;

  gap: 1.6rem;

  & p {
    ${({ theme }) => theme.TYPOGRAPHY.B4_R};
    color: ${({ theme }) => theme.FONT.B02};
    line-height: 1.9rem;
    text-align: center;
  }
`;
