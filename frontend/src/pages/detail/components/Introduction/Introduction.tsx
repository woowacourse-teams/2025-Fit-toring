import styled from '@emotion/styled';

interface IntroductionProps {
  content: string;
}

function Introduction({ content }: IntroductionProps) {
  return (
    <StyledContainer>
      <StyledH4>멘토 소개</StyledH4>
      {content}
    </StyledContainer>
  );
}

export default Introduction;

const StyledContainer = styled.div`
  width: 100%;
  padding: 0 1rem;

  color: ${({ theme }) => theme.FONT.B02};
  white-space: pre-line;
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const StyledH4 = styled.h4`
  display: flex;
  justify-content: center;

  margin-bottom: 1.7rem;

  ${({ theme }) => theme.TYPOGRAPHY.H4_R}
  color: ${({ theme }) => theme.FONT.B01};
`;
