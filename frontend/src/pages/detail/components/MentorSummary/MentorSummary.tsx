import styled from '@emotion/styled';

import type { Certificates } from '../../../../common/types/MentoringDetail';

interface MentorSummaryProps {
  introduction: string;
  career: number;
  certificates: Certificates[];
}

function MentorSummary({
  introduction,
  career,
  certificates,
}: MentorSummaryProps) {
  return (
    <StyledContainer>
      <StyledSelfIntroduction>{introduction}</StyledSelfIntroduction>
      <StyledCertifications>
        <p>경력: {career}년 </p>
        <p>
          자격증:{' '}
          {certificates.map((certificate) => certificate.title).join(', ')}
        </p>
      </StyledCertifications>
      <StyledHr />
    </StyledContainer>
  );
}

export default MentorSummary;

const StyledContainer = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.2rem;

  width: 100%;
  padding: 0;
`;

const StyledSelfIntroduction = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B2_B}
  color: ${({ theme }) => theme.FONT.B03}
`;

const StyledCertifications = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.1rem;

  width: 100%;

  > p {
    width: 100%;
  }

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
  color: ${({ theme }) => theme.FONT.B02}
`;

const StyledHr = styled.hr`
  width: 100%;
  height: 0.1rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;
