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
      <StyledProfileImage
        src={mentorProfileImage || defaultImage}
        alt={`${mentorName} 멘토`}
        onError={(e) => {
          e.currentTarget.src = defaultImage;
        }}
      />
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

const StyledProfileImage = styled.img`
  width: 4.8rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 50%;
  aspect-ratio: 1 / 1;
`;
