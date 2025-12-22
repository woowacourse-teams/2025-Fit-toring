import styled from '@emotion/styled';

interface EmptyDataProps {
  icon?: string;
  title: string;
  description?: string;
}

function EmptyData({ icon = '🔍', title, description }: EmptyDataProps) {
  return (
    <S_Container>
      <S_Icon>{icon}</S_Icon>
      <S_Title>{title}</S_Title>
      {description && <S_Description>{description}</S_Description>}
    </S_Container>
  );
}

export default EmptyData;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.6rem;

  width: 100%;
  padding: 8rem 2rem;
`;

const S_Icon = styled.div`
  font-size: 4.8rem;
`;

const S_Title = styled.h3`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H3_B};
`;

const S_Description = styled.p`
  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  text-align: center;
`;
