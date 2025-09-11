import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import humanIcon from '../../../../common/assets/images/human.svg';
import Button from '../../../../common/components/Button/Button';
import MentoringStepper from '../../../../common/components/mentoringStepper/MentoringStepper/MentoringStepper';
import Modal from '../../../../common/components/Modal/Modal';
import { PAGE_URL } from '../../../../common/constants/url';
import { THEME } from '../../../../common/styles/theme';
import { StatusTypeEnum } from '../../../../common/types/statusType';

import type { MentoringDetail } from '../../../../common/types/MentoringDetail';

interface CompleteModalProps {
  opened: boolean;
  onCloseClick: () => void;
  mentorInfo: MentoringDetail | null;
}

function CompleteModal({
  opened,
  onCloseClick,
  mentorInfo,
}: CompleteModalProps) {
  const navigate = useNavigate();

  const handleMentorSearchClick = () => {
    onCloseClick();
    navigate(PAGE_URL.HOME);
  };

  const handleGoReservationClick = () => {
    onCloseClick();
    navigate(PAGE_URL.PARTICIPATED_MENTORING);
  };

  return (
    <Modal opened={opened} onCloseClick={onCloseClick}>
      <StyledContainer>
        <StyledColoredBackground />
        <StyledTitle>멘토링 신청이 완료되었습니다.</StyledTitle>
        <StyledStepperWrapper>
          <StyledReservationInfoText>
            확정 완료 시 문자로 <br /> 연락용 오픈카톡방 링크가 발송됩니다.
          </StyledReservationInfoText>
          <MentoringStepper status={StatusTypeEnum.PENDING} />
        </StyledStepperWrapper>
        <StyledMentorInfoBox>
          <StyledInfoTextWithIcon>
            <StyledIcon src={humanIcon} alt="사람 아이콘" />
            <span>멘토 정보</span>
          </StyledInfoTextWithIcon>
          <p>이름: {mentorInfo?.mentorName}</p>
          <p>전문분야: {mentorInfo?.categories.join(' / ')}</p>
        </StyledMentorInfoBox>
        <Button
          onClick={handleGoReservationClick}
          size="full"
          customStyle={css`
            padding: 0.8rem;

            background-color: ${THEME.SYSTEM.MAIN500};

            font-size: 1.2rem;
          `}
        >
          예약 내역 보러가기
        </Button>
        <Button
          onClick={handleMentorSearchClick}
          size="full"
          customStyle={css`
            padding: 0.8rem;
            border: 1px solid ${THEME.OUTLINE.DARK};

            background-color: ${THEME.BG.WHITE};

            color: ${THEME.FONT.B02};
            font-size: 1.2rem;
          `}
        >
          다른 멘토 찾기
        </Button>
      </StyledContainer>
    </Modal>
  );
}
export default CompleteModal;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;

  padding-top: 4rem;
`;

const StyledColoredBackground = styled.div`
  position: absolute;
  top: 0;
  left: 0;
  z-index: -1;

  width: 100%;
  height: 30%;
  border-radius: 5px 5px 0 0;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN500};
`;

const StyledTitle = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R};
  color: ${({ theme }) => theme.FONT.W01};
`;

const StyledMentorInfoBox = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.4rem;

  background-color: rgb(255 255 255 / 10%);

  & > p {
    ${({ theme }) => theme.TYPOGRAPHY.B4_R};
    color: ${({ theme }) => theme.FONT.B01};
  }
`;

const StyledInfoTextWithIcon = styled.div`
  display: flex;
  gap: 0.7rem;

  & > span {
    ${({ theme }) => theme.TYPOGRAPHY.B4_R};
    color: ${({ theme }) => theme.FONT.B01};
  }
`;

const StyledIcon = styled.img`
  width: 1.4rem;
  height: 1.4rem;
`;

const StyledStepperWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  padding: 3rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.LIGHT};
  border-radius: 9px;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const StyledReservationInfoText = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B4_B};
  color: ${({ theme }) => theme.FONT.B02};
  text-align: center;
`;
