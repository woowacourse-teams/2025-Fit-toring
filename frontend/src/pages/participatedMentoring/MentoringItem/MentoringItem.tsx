import styled from '@emotion/styled';

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
  return <StyledContainer key={mentorName} />;
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
