import React from 'react';

import styled from '@emotion/styled';

function MentorSummary() {
  return (
    <StyledContainer>
      <StyledSelfIntroduction>
        5년차 전문 트레이너로 개인 맞춤 운동 및 식단 코칭을 제공합니다.
      </StyledSelfIntroduction>
      <StyledCertifications>
        <p>경력: 전문 트레이너 5년 </p>
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
  margin-top: 3rem;
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
