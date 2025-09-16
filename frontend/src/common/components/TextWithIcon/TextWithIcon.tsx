import styled from '@emotion/styled';

interface TextWithIconProps {
  text: string;
  iconSrc: string;
  iconName: string;
}

function TextWithIcon({ text, iconSrc, iconName }: TextWithIconProps) {
  return (
    <S_Container>
      <S_Img alt={`${iconName} 아이콘`} src={iconSrc} />
      <S_Span>{text}</S_Span>
    </S_Container>
  );
}

export default TextWithIcon;

const S_Container = styled.div`
  display: flex;
  align-items: center;
  gap: 0.3rem;
`;

const S_Img = styled.img`
  width: 1.4rem;
  height: 1.4rem;
`;

const S_Span = styled.span`
  display: flex;

  color: ${({ theme }) => theme.SYSTEM.GRAY600};
  ${({ theme }) => theme.TYPOGRAPHY.C4_R};
`;
