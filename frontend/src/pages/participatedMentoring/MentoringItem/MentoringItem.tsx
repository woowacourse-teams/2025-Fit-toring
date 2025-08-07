import styled from '@emotion/styled';

import defaultImage from '../../../common/assets/images/profileImg.svg';
import MentoringApplicationStatus from '../../createdMentoring/components/MentoringApplicationStatus/MentoringApplicationStatus';
import ReviewButton from '../ReviewButton/ReviewButton';

import type { ParticipatedMentoringType } from '../types/participatedMentoring';
interface MentoringItemProps {
  mentoring: ParticipatedMentoringType;
}

const TIME = '15';

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
        <ReviewButton isReviewed={isReviewed} status={status} />
      </StyledApplicationInfoWrapper>
    </StyledContainer>
  );
}

export default MentoringItem;

const StyledContainer = styled.li`
  display: flex;
  flex-direction: column;
  gap: 1rem;

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

  height: 12rem;
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

  width: 7rem;
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
