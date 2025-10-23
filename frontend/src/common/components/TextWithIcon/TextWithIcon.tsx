import styled from '@emotion/styled';

interface TextWithIconProps {
  ariaLabel: string;
  text: string;
  iconSrc: string;
}

function TextWithIcon({ ariaLabel, text, iconSrc }: TextWithIconProps) {
  return (
    <S_Container role="text" aria-label={ariaLabel}>
      <S_Img aria-hidden="true" alt="" src={iconSrc} />
      <S_Span aria-hidden="true">{text}</S_Span>
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
  ${({ theme }) => theme.TYPOGRAPHY.C2_R};
`;
