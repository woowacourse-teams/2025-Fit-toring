import styled from '@emotion/styled';

interface MyPageBadgeProps {
  label: string;
  backgroundColor?: string;
  borderColor?: string;
  color?: string;
}

function MyPageBadge({
  label,
  backgroundColor = 'transparent',
  borderColor,
  color,
}: MyPageBadgeProps) {
  return (
    <S_Badge
      backgroundColor={backgroundColor}
      borderColor={borderColor}
      color={color}
    >
      {label}
    </S_Badge>
  );
}

export default MyPageBadge;

const S_Badge = styled.span<{
  backgroundColor: string;
  borderColor?: string;
  color?: string;
}>`
  flex-shrink: 0;

  padding: 0.4rem 1rem;
  border: 1px solid
    ${({ borderColor, color, theme }) => borderColor ?? color ?? theme.SYSTEM.MAIN500};
  border-radius: 999px;

  background-color: ${({ backgroundColor }) => backgroundColor};

  color: ${({ color, theme }) => color ?? theme.SYSTEM.MAIN500};
  ${({ theme }) => theme.TYPOGRAPHY.B4_SB}
`;
