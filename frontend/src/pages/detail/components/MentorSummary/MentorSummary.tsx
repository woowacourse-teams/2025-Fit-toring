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
    <S_Container>
      <S_SelfIntroduction>{introduction}</S_SelfIntroduction>
      <S_Certifications>
        <p>경력: {career}년 </p>
        <p>
          자격증:{' '}
          {certificates.map((certificate) => certificate.title).join(', ')}
        </p>
      </S_Certifications>
      <S_Hr />
    </S_Container>
  );
}

export default MentorSummary;

const S_Container = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.2rem;

  width: 100%;
  padding: 0;
`;

const S_SelfIntroduction = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B2_B}
  color: ${({ theme }) => theme.FONT.B03}
`;

const S_Certifications = styled.div`
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

const S_Hr = styled.hr`
  width: 100%;
  height: 0.1rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;
