import { useState } from 'react';

import styled from '@emotion/styled';

import defaultImage from '../../../common/assets/images/profileImg.svg';
import MentoringApplicationStatus from '../../../common/components/MentoringApplicationStatus/MentoringApplicationStatus';
import MentoringStepper from '../../../common/components/mentoringStepper/MentoringStepper/MentoringStepper';
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
    <StyledContainer key={reservationId}>
      <StyledMentorInfoWrapper>
        <StyledProfileImage
          src={mentorProfileImage || defaultImage}
          alt={`${mentorName} 멘토`}
          onError={(e) => {
            e.currentTarget.src = defaultImage;
          }}
        />
        <StyledMentoringInfo>
          <StyledName>{mentorName} 멘토</StyledName>
          <StyledCategoryWrapper>
            {categories.map((category) => (
              <StyledCategory key={category}>{category}</StyledCategory>
            ))}
          </StyledCategoryWrapper>
        </StyledMentoringInfo>
        <StyledStatusWrapper>
          <MentoringApplicationStatus status={status} />
        </StyledStatusWrapper>
      </StyledMentorInfoWrapper>
      <StyeldReservationInfoText>
        확정 완료 시 문자로 <br /> 연락용 오픈카톡방 링크가 발송됩니다.
      </StyeldReservationInfoText>
      {status !== 'REJECTED' ? (
        <StyledStepperWrapper>
          <MentoringStepper status={status} />
        </StyledStepperWrapper>
      ) : null}
      <StyledApplicationInfoWrapper>
        <StyledApplicationDate>⏰ {reservedAt}</StyledApplicationDate>
        <StyledApplicationPrice>
          💰 {TIME}분 {price.toLocaleString()}원
        </StyledApplicationPrice>
        <ReviewButton
          isReviewed={isReviewed}
          status={status}
          onReviewButtonClick={handleReviewModalToggle}
        />
      </StyledApplicationInfoWrapper>
      <ReviewModal
        reservationId={reservationId}
        mentorName={mentorName}
        opened={opened}
        onCloseClick={handleReviewModalToggle}
        onReviewSubmitButtonClick={handleReviewSubmitButtonClick}
      />
    </StyledContainer>
  );
}

export default MentoringItem;

const StyledContainer = styled.li`
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

const StyledMentorInfoWrapper = styled.div`
  display: flex;
  gap: 1.2rem;
`;

const StyledProfileImage = styled.img`
  width: 4.8rem;
  height: 4.8rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 50%;

  aspect-ratio: 1/1;
  object-fit: cover;
`;

const StyledName = styled.h4`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_R}
`;

const StyledMentoringInfo = styled.div`
  display: flex;
  flex-flow: column wrap;
  flex-grow: 1;
  gap: 1rem;
`;

const StyledCategoryWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
`;

const StyledCategory = styled.span`
  display: inline-flex;
  align-items: center;
  justify-content: center;

  padding: 0.2rem 0.4rem;
  border-radius: 6px;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN100};

  color: ${({ theme }) => theme.SYSTEM.MAIN600};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledStatusWrapper = styled.div`
  height: auto;
`;

const StyeldReservationInfoText = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B4_B};
  color: ${({ theme }) => theme.FONT.B02};
  text-align: center;
`;

const StyledStepperWrapper = styled.div`
  width: 90%;
  margin: 0 auto;
`;

const StyledApplicationInfoWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 1rem;
`;

const StyledApplicationDate = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledApplicationPrice = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
