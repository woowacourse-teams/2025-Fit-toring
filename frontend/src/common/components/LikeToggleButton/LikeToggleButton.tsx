import styled from '@emotion/styled';

import thumbsUpIcon from '../../assets/images/thumbsUpIcon.svg';

interface LikeToggleButtonProps {
  count: number;
  pressed: boolean;
  ariaLabel: string;
  size?: 'small' | 'medium';
  disabled?: boolean;
  onClick: () => void;
}

function LikeToggleButton({
  count,
  pressed,
  ariaLabel,
  size = 'medium',
  disabled = false,
  onClick,
}: LikeToggleButtonProps) {
  return (
    <S_Button
      type="button"
      aria-label={ariaLabel}
      aria-pressed={pressed}
      size={size}
      pressed={pressed}
      disabled={disabled}
      onClick={onClick}
    >
      <S_Icon aria-hidden="true" size={size} />
      <S_Count size={size}>{count}</S_Count>
    </S_Button>
  );
}

export default LikeToggleButton;

const S_Button = styled.button<{ pressed: boolean; size: 'small' | 'medium' }>`
  display: inline-flex;
  align-items: center;
  align-self: flex-start;
  gap: ${({ size }) => (size === 'small' ? '0.5rem' : '0.65rem')};

  padding: ${({ size }) =>
    size === 'small' ? '0.75rem 1rem' : '0.8rem 1.2rem'};
  border: none;
  border-radius: 999px;

  background-color: ${({ theme, pressed }) =>
    pressed ? theme.SYSTEM.MAIN500 : theme.SYSTEM.GRAY50};

  color: ${({ theme, pressed }) => (pressed ? theme.FONT.W01 : theme.FONT.G01)};
  cursor: pointer;

  &:disabled {
    cursor: not-allowed;
    opacity: 0.72;
  }
`;

const S_Icon = styled.span<{ size: 'small' | 'medium' }>`
  display: block;
  flex-shrink: 0;

  width: ${({ size }) => (size === 'small' ? '1.3rem' : '1.4rem')};
  height: ${({ size }) => (size === 'small' ? '1.3rem' : '1.4rem')};

  background-color: currentcolor;
  mask-image: url(${thumbsUpIcon});
  mask-repeat: no-repeat;
  mask-position: center;
  mask-size: contain;
`;

const S_Count = styled.span<{ size: 'small' | 'medium' }>`
  ${({ theme, size }) =>
    size === 'small' ? theme.TYPOGRAPHY.C3_R : theme.TYPOGRAPHY.B3_R}
`;
