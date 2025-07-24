import React from 'react';

import styled from '@emotion/styled';

interface MentorSummaryProps {
  introduction: string;
  career: number;
}

function MentorSummary({ introduction, career }: MentorSummaryProps) {
  return (
    <StyledContainer>
      <StyledSelfIntroduction>{introduction}</StyledSelfIntroduction>
      <StyledCertifications>
        <p>경력: 전문 트레이너 {career}년 </p>
        <p>자격증: 생활스포츠지도사, 운동처방사</p>
      </StyledCertifications>
      <StyledHr />
    </StyledContainer>
  );
}

export default MentorSummary;

const StyledContainer = styled.section`
  display: flex;
  width: 100%;
  padding: 1rem;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
`;
const StyledSelfIntroduction = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B2_B}
  color: ${({ theme }) => theme.FONT.B02}
`;

const StyledCertifications = styled.p`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.1rem;

  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
  color: ${({ theme }) => theme.FONT.B02}
`;

const StyledHr = styled.hr`
  width: 100%;
  height: 0.1rem;
  border: 1px solid ${({ theme }) => theme.LINE.REGULAR};
`;
