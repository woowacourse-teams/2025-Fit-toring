import styled from '@emotion/styled';

function ParticipatedMentoring() {
  return (
    <StyledContainer>
      <StyledTitle>참여한 멘토링</StyledTitle>
    </StyledContainer>
  );
}

export default ParticipatedMentoring;

const StyledContainer = styled.section`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  width: 100%;
  height: 100%;
  padding: 2rem;
`;

const StyledTitle = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB3_R}
`;
