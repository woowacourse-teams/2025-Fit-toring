import styled from '@emotion/styled';

import defaultImage from '../../../common/assets/images/profileImg.svg';
import MentoringApplicationStatus from '../../CreatedMentoring/components/MentoringApplicationStatus/MentoringApplicationStatus';
import { StatusTypeEnum } from '../../CreatedMentoring/types/statusType';

import type { ParticipatedMentoringType } from '../types/participatedMentoring';
interface MentoringItemProps {
  mentoring: ParticipatedMentoringType;
}

function MentoringItem({
  mentoring: {
    mentorName,
    mentorProfileImage,
    price,
    reservedAt,
    categories,
    isReviewed,
    status,
  },
}: MentoringItemProps) {
  const TIME = '15';

  const canWriteReview = !isReviewed && status === StatusTypeEnum.completed;

  return (
    <StyledContainer key={mentorName}>
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
      <StyledApplicationInfoWrapper>
        <StyledApplicationDate>⏰ {reservedAt}</StyledApplicationDate>
        <StyledApplicationPrice>
          💰 {TIME}분 {price.toLocaleString()}원
        </StyledApplicationPrice>
        {canWriteReview && <StyledReviewButton>리뷰 작성</StyledReviewButton>}
      </StyledApplicationInfoWrapper>
    </StyledContainer>
  );
}

export default MentoringItem;

const StyledContainer = styled.li`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  height: auto;
  padding: 1.5rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;

  transition: all 0.2s ease;

  :hover {
    box-shadow: 0 0.4rem 1.6rem rgb(0 0 0 / 10%);
  }

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledMentorInfoWrapper = styled.div`
  display: flex;
  gap: 1.2rem;
`;

const StyledProfileImage = styled.img`
  width: 4.8rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 50%;
  aspect-ratio: 1 / 1;
`;

const StyledName = styled.h4`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_R}
`;

const StyledMentoringInfo = styled.div`
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  gap: 1rem;
`;

const StyledCategoryWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
`;

const StyledCategory = styled.span`
  padding: 0.2rem 0.4rem;
  border-radius: 6px;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN100};

  color: ${({ theme }) => theme.SYSTEM.MAIN600};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledStatusWrapper = styled.div`
  height: auto;
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

const StyledReviewButton = styled.button`
  display: flex;
  align-items: center;
  gap: 0.4rem;

  margin-left: auto;
  padding: 0.4rem 0.8rem;
  border: none;
  border-radius: 6px;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN700};

  color: ${({ theme }) => theme.FONT.W01};

  transition: all 0.2s ease;

  :hover {
    background-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }

  cursor: pointer;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
