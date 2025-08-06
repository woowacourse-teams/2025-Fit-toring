import styled from '@emotion/styled';

import defaultImage from '../../../common/assets/images/profileImg.svg';

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
        <StyledName>{mentorName} 멘토</StyledName>
      </StyledMentorInfoWrapper>
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

  /* align-items: center; */
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
  ${({ theme }) => theme.TYPOGRAPHY.B1_R}
`;
