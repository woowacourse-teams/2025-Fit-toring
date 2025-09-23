import { useState } from 'react';

import styled from '@emotion/styled';

import defaultImage from '../../../common/assets/images/profileImg.svg';
import MentoringApplicationStatus from '../../../common/components/MentoringApplicationStatus/MentoringApplicationStatus';
import MentoringStepper from '../../../common/components/mentoringStepper/MentoringStepper/MentoringStepper';
import { StatusTypeEnum } from '../../../common/types/statusType';
import ReviewButton from '../ReviewButton/ReviewButton';
import ReviewModal from '../ReviewModal/ReviewModal';

import type { ParticipatedMentoringType } from '../types/participatedMentoring';
interface MentoringItemProps {
  mentoring: ParticipatedMentoringType;
  handleReviewSubmitButtonClick: (reservationId: number) => void;
}

const TIME = '15';

function MentoringItem({
  mentoring: {
    reservationId,
    mentorName,
    mentorProfileImage,
    price,
    reservedAt,
    categories,
    isReviewed,
    status,
  },
  handleReviewSubmitButtonClick,
}: MentoringItemProps) {
  const [opened, setOpened] = useState(false);

  const handleReviewModalToggle = () => {
    setOpened((prev) => !prev);
  };

  return (
    <S_Container key={reservationId}>
      <S_MentorInfoWrapper>
        <S_ProfileImage
          src={mentorProfileImage || defaultImage}
          alt={`${mentorName} 멘토`}
          onError={(e) => {
            e.currentTarget.src = defaultImage;
          }}
        />
        <S_MentoringInfo>
          <S_Name>{mentorName} 멘토</S_Name>
          <S_CategoryWrapper>
            {categories.map((category) => (
              <S_Category key={category}>{category}</S_Category>
            ))}
          </S_CategoryWrapper>
        </S_MentoringInfo>
        <S_StatusWrapper>
          <MentoringApplicationStatus status={status} />
        </S_StatusWrapper>
      </S_MentorInfoWrapper>

      {status !== StatusTypeEnum.REJECTED ? (
        <S_StepperWrapper>
          <MentoringStepper status={status} />
        </S_StepperWrapper>
      ) : null}
      <S_ApplicationInfoWrapper>
        <S_ApplicationDate>⏰ {reservedAt}</S_ApplicationDate>
        <S_ApplicationPrice>
          💰 {TIME}분 {price.toLocaleString()}원
        </S_ApplicationPrice>
        <ReviewButton
          isReviewed={isReviewed}
          status={status}
          onReviewButtonClick={handleReviewModalToggle}
        />
      </S_ApplicationInfoWrapper>
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
  gap: 1.3rem;

  padding: 1.5rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;

  transition: all 0.2s ease;

  :hover {
    box-shadow: 0 4px 16px rgb(0 0 0 / 10%);
  }

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const S_MentorInfoWrapper = styled.div`
  display: flex;
  gap: 1.2rem;
`;

const S_ProfileImage = styled.img`
  width: 4.8rem;
  height: 4.8rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 50%;

  aspect-ratio: 1/1;
  object-fit: cover;
`;

const S_Name = styled.h4`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_R}
`;

const S_MentoringInfo = styled.div`
  display: flex;
  flex-flow: column wrap;
  flex-grow: 1;
  gap: 1rem;
`;

const S_CategoryWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
`;

const S_Category = styled.span`
  display: inline-flex;
  align-items: center;
  justify-content: center;

  padding: 0.2rem 0.4rem;
  border-radius: 6px;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN100};

  color: ${({ theme }) => theme.SYSTEM.MAIN600};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const S_StatusWrapper = styled.div`
  height: auto;
`;

const S_StepperWrapper = styled.div`
  width: 90%;
  margin: 0 auto;
`;

const S_ApplicationInfoWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 1rem;
`;

const S_ApplicationDate = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const S_ApplicationPrice = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
