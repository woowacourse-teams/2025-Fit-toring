import { useState } from 'react';

import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import MentoringApplicationStatus from '../../../common/components/MentoringApplicationStatus/MentoringApplicationStatus';
import MentoringStepper from '../../../common/components/mentoringStepper/MentoringStepper/MentoringStepper';
import { PAGE_URL } from '../../../common/constants/url';
import { StatusTypeEnum } from '../../../common/types/statusType';
import ReviewButton from '../ReviewButton/ReviewButton';
import ReviewModal from '../ReviewModal/ReviewModal';

import type { ParticipatedMentoringType } from '../types/participatedMentoring';
import Button from '../../../common/components/Button/Button';
interface MentoringItemProps {
  mentoring: ParticipatedMentoringType;
  handleReviewSubmitButtonClick: (reservationId: number) => void;
}

function MentoringItem({
  mentoring: {
    reservationId,
    mentoringId,
    mentorName,
    mentorProfileImage,
    content,
    reservedAt,
    isReviewed,
    status,
    chatRoomId,
  },
  handleReviewSubmitButtonClick,
}: MentoringItemProps) {
  const [opened, setOpened] = useState(false);

  const navigate = useNavigate();

  const handleReviewButtonClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.stopPropagation();
    handleReviewModalToggle();
  };
  const handleReviewModalToggle = () => {
    setOpened((prev) => !prev);
  };

  const handleMentoringCardClick = () => {
    navigate(`${PAGE_URL.DETAIL}/${mentoringId}`);
  };

  const handleReviewCompleteButtonClick = (
    event: React.MouseEvent<HTMLButtonElement>,
  ) => {
    event.stopPropagation();
    navigate(`${PAGE_URL.DETAIL}/${mentoringId}`, {
      state: { tab: 'review' },
    });
  };

  const handleChatButtonClick = (
    event: React.MouseEvent<HTMLButtonElement>,
  ) => {
    event.stopPropagation();
    navigate(`${PAGE_URL.CHAT_ROOM}/${chatRoomId}`);
  };

  return (
    <>
      <S_Container key={reservationId} onClick={handleMentoringCardClick}>
        <S_SummaryWrapper>
          <S_Name>{mentorName}</S_Name>
          <MentoringApplicationStatus status={status} type="PARTICIPATED" />
        </S_SummaryWrapper>
        <S_ReservedAt>신청일: {reservedAt}</S_ReservedAt>
        <S_MentorCardWrapper>
          <S_ProfileImage src={mentorProfileImage} />
          <S_MessagWrapper>
            <p>{content}</p>
          </S_MessagWrapper>
        </S_MentorCardWrapper>
        {status !== StatusTypeEnum.REJECTED ? (
          <ReviewButton
            isReviewed={isReviewed}
            status={status}
            onReviewButtonClick={handleReviewButtonClick}
            onReviewCompleteButtonClick={handleReviewCompleteButtonClick}
          />
        ) : null}
        {status === StatusTypeEnum.APPROVED ||
        status === StatusTypeEnum.COMPLETE ? (
          <S_ChatButton onClick={handleChatButtonClick}>
            채팅방으로 이동
          </S_ChatButton>
        ) : null}
        {status !== StatusTypeEnum.REJECTED &&
        status !== StatusTypeEnum.COMPLETE ? (
          <S_StepperWrapper>
            <MentoringStepper status={status} />
          </S_StepperWrapper>
        ) : null}
      </S_Container>

      <ReviewModal
        reservationId={reservationId}
        mentorName={mentorName}
        opened={opened}
        onCloseClick={handleReviewModalToggle}
        onReviewSubmitButtonClick={handleReviewSubmitButtonClick}
      />
    </>
  );
}

export default MentoringItem;

const S_Container = styled.li`
  display: flex;
  flex-direction: column;

  padding: 2.4rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-radius: 5px;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}

  cursor: pointer;
`;

const S_SummaryWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;

  width: 100%;
  margin-bottom: 0.9rem;
`;

const S_Name = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.LB3_SB}
`;

const S_ReservedAt = styled.p`
  margin-bottom: 1.6rem;

  color: ${({ theme }) => theme.SYSTEM.GRAY500};

  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;

const S_MentorCardWrapper = styled.div`
  display: flex;
  justify-content: space-between;

  width: 100%;
  margin-bottom: 1rem;
`;

const S_ProfileImage = styled.img`
  width: 12rem;
  height: 14rem;
  border-radius: 5px;

  aspect-ratio: 120/ 140;
  object-fit: cover;
`;

const S_MessagWrapper = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  width: 100%;
  height: 8.5rem;
  min-width: 0;
  padding-left: 1.2rem;

  color: ${({ theme }) => theme.SYSTEM.GRAY800};
  overflow-y: scroll;

  ${({ theme }) => theme.TYPOGRAPHY.C2_R}
  line-height: 1.8rem;
`;

const S_ChatButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;

  width: 100%;
  height: 3.8rem;
  margin-top: 1rem;
  margin-left: auto;
  padding: 0.4rem 0.8rem;
  border: none;
  border-radius: 6px;

  transition: all 0.2s ease;

  cursor: pointer;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
  background-color: ${({ theme }) => theme.BG.BLACK};

  color: ${({ theme }) => theme.BG.WHITE};
`;

const S_StepperWrapper = styled.div`
  display: flex;
  justify-content: center;

  width: 100%;
`;
