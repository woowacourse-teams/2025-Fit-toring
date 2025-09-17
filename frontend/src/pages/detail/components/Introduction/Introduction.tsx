import styled from '@emotion/styled';

interface IntroductionProps {
  content: string;
}

function Introduction({ content }: IntroductionProps) {
  return (
    <S_Container>
      <S_H4>멘토 소개</S_H4>
      {content}
    </S_Container>
  );
}

export default Introduction;

const S_Container = styled.div`
  width: 100%;
  padding: 0 1rem;

  color: ${({ theme }) => theme.FONT.B02};
  white-space: pre-line;
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const S_H4 = styled.h4`
  display: flex;
  justify-content: center;

  margin-bottom: 1.7rem;

  ${({ theme }) => theme.TYPOGRAPHY.H4_R}
  color: ${({ theme }) => theme.FONT.B01};
`;
