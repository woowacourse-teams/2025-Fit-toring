import styled from '@emotion/styled';

interface MentorOverviewProps {
  mentorCount: number;
}

function MentorOverview({ mentorCount }: MentorOverviewProps) {
  return (
    <StyledContainer>
      <StyledMentorCount>멘토 ({mentorCount}명)</StyledMentorCount>
      <StyledServiceDescription>
        15분 집중 상담으로 빠르고 효율적인 피트니스 멘토링을 경험해보세요
      </StyledServiceDescription>
    </StyledContainer>
  );
}

export default MentorOverview;

const StyledContainer = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
`;

const StyledMentorCount = styled.h2`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
  color: ${({ theme }) => theme.FONT.B01};
`;

const StyledServiceDescription = styled.p`
  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;
