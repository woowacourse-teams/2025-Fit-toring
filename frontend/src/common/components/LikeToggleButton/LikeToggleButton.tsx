import styled from '@emotion/styled';

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
      <S_Icon size={size} viewBox="0 0 15 20" aria-hidden="true" focusable="false">
        <path d="M14.1501 14.2652L14.8551 10.1852C14.8965 9.94607 14.8851 9.70078 14.8218 9.4665C14.7584 9.23222 14.6447 9.01461 14.4885 8.82888C14.3323 8.64314 14.1374 8.49378 13.9174 8.39124C13.6975 8.28869 13.4578 8.23544 13.2151 8.2352H8.03409C7.91317 8.23523 7.7937 8.20893 7.68398 8.15814C7.57425 8.10734 7.4769 8.03327 7.39868 7.94106C7.32046 7.84886 7.26325 7.74073 7.23103 7.62419C7.19881 7.50765 7.19234 7.38549 7.21208 7.2662L7.87508 3.2212C7.98218 2.56441 7.95153 1.89252 7.78508 1.2482C7.71323 0.982209 7.57552 0.738606 7.38466 0.539886C7.19381 0.341166 6.95596 0.193734 6.69308 0.111202L6.54809 0.0642016C6.2202 -0.0406978 5.86463 -0.0163729 5.55408 0.132202C5.21408 0.296202 4.96609 0.595202 4.87409 0.950202L4.39808 2.7842C4.2468 3.36791 4.02658 3.93154 3.74209 4.4632C3.32709 5.2402 2.68508 5.8632 2.01708 6.4382L0.578085 7.6782C0.378454 7.85071 0.222538 8.06807 0.123113 8.31246C0.0236879 8.55685 -0.0164169 8.82132 0.00608477 9.0842L0.818085 18.4772C0.853874 18.8926 1.04413 19.2795 1.35127 19.5615C1.65842 19.8434 2.06014 20 2.47708 20.0002H7.12508C10.6071 20.0002 13.5781 17.5742 14.1501 14.2652Z" />
      </S_Icon>
      <S_Count size={size}>{count}</S_Count>
    </S_Button>
  );
}

export default LikeToggleButton;

const S_Button = styled.button<{ pressed: boolean; size: 'small' | 'medium' }>`
  display: inline-flex;
  align-items: center;
  gap: ${({ size }) => (size === 'small' ? '0.6rem' : '0.8rem')};
  align-self: flex-start;

  padding: ${({ size }) => (size === 'small' ? '0.7rem 1.2rem' : '0.9rem 1.8rem')};
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY100};
  border-radius: 999px;

  background-color: ${({ theme }) => theme.BG.WHITE};
  color: ${({ theme, pressed }) =>
    pressed ? theme.SYSTEM.MAIN600 : theme.FONT.B02};
  cursor: pointer;

  &:disabled {
    cursor: not-allowed;
    opacity: 0.72;
  }
`;

const S_Icon = styled.svg<{ size: 'small' | 'medium' }>`
  width: ${({ size }) => (size === 'small' ? '1.5rem' : '1.8rem')};
  height: ${({ size }) => (size === 'small' ? '1.5rem' : '1.8rem')};
  flex-shrink: 0;

  fill: currentColor;
`;

const S_Count = styled.span<{ size: 'small' | 'medium' }>`
  ${({ theme, size }) =>
    size === 'small' ? theme.TYPOGRAPHY.B4_R : theme.TYPOGRAPHY.B2_R}
`;
