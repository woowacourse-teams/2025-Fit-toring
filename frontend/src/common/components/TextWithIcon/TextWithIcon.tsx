import styled from '@emotion/styled';

interface TextWithIconProps {
  text: string;
  iconSrc: string;
  iconName: string;
}

function TextWithIcon({ text, iconSrc, iconName }: TextWithIconProps) {
  return (
    <StyledContainer>
      <StyledImg alt={`${iconName} 아이콘`} src={iconSrc} />
      <StyledSpan>{text}</StyledSpan>
    </StyledContainer>
  );
}

export default TextWithIcon;

const StyledContainer = styled.div`
  display: flex;
  gap: 0.3rem;
  align-items: center;
`;

const StyledImg = styled.img`
  width: 1.4rem;
  height: 1.4rem;
`;

const StyledSpan = styled.span`
  color: ${({ theme }) => theme.FONT.B04};
  font-size: 1.2rem;
  display: flex;
`;
