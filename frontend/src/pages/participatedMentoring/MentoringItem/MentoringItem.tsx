import { useState } from 'react';

import styled from '@emotion/styled';

import MentoringApplicationStatus from '../../../common/components/MentoringApplicationStatus/MentoringApplicationStatus';
import MentoringStepper from '../../../common/components/mentoringStepper/MentoringStepper/MentoringStepper';
import { StatusTypeEnum } from '../../../common/types/statusType';
import ReviewButton from '../ReviewButton/ReviewButton';
import ReviewModal from '../ReviewModal/ReviewModal';

import type { ParticipatedMentoringType } from '../types/participatedMentoring';
import { useNavigate } from 'react-router-dom';
import { PAGE_URL } from '../../../common/constants/url';
interface MentoringItemProps {
  mentoring: ParticipatedMentoringType;
  handleReviewSubmitButtonClick: (reservationId: number) => void;
}

const TIME = '15';

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
  },
  handleReviewSubmitButtonClick,
}: MentoringItemProps) {
  const [opened, setOpened] = useState(false);

  const navigate = useNavigate();

  const handleReviewModalToggle = () => {
    setOpened((prev) => !prev);
  };

  const handleReviewCompleteButtonClick = () => {
    navigate(`${PAGE_URL.DETAIL}/${mentoringId}`, {
      state: { tab: 'review' },
    });
  };

  return (
    <S_Container key={reservationId}>
      <S_SummaryWrapper>
        <S_Name>{mentorName}</S_Name>
        <MentoringApplicationStatus status={status} type="PARTICIPATED" />
      </S_SummaryWrapper>
      <S_ReservedAt>신청일: {reservedAt}</S_ReservedAt>
      <S_MentorCardWrapper>
        <S_ProfileImage src={mentorProfileImage}></S_ProfileImage>
        <S_MessageAndReviewWrapper>
          <S_Message>{content}</S_Message>
          {status !== StatusTypeEnum.REJECTED ? (
            <ReviewButton
              isReviewed={isReviewed}
              status={status}
              onReviewButtonClick={handleReviewModalToggle}
              onReviewCompleteButtonClick={handleReviewCompleteButtonClick}
            />
          ) : null}
        </S_MessageAndReviewWrapper>
      </S_MentorCardWrapper>
      {status !== StatusTypeEnum.REJECTED &&
      status !== StatusTypeEnum.COMPLETE ? (
        <S_StepperWrapper>
          <MentoringStepper status={status} />
        </S_StepperWrapper>
      ) : null}
      <ReviewModal
        reservationId={reservationId}
        mentorName={mentorName}
        opened={opened}
        onCloseClick={handleReviewModalToggle}
        onReviewSubmitButtonClick={handleReviewSubmitButtonClick}
      />
    </S_Container>
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
`;

const S_ProfileImage = styled.img`
  width: 12rem;
  height: 14rem;
  border-radius: 5px;

  aspect-ratio: 120/ 140;
  object-fit: cover;
`;

const S_MessageAndReviewWrapper = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  width: 100%;
  padding-left: 1.2rem;
`;

const S_Message = styled.div`
  display: flex;

  width: 100%;
  height: 8.5rem;

  color: ${({ theme }) => theme.SYSTEM.GRAY800};
  overflow-y: scroll;

  ${({ theme }) => theme.TYPOGRAPHY.C2_R}
  line-height: 1.8rem;
`;

const S_StepperWrapper = styled.div`
  display: flex;
  justify-content: center;

  width: 100%;
`;
