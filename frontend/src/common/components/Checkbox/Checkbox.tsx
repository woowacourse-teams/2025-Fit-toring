import type { ChangeEventHandler } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

interface CheckboxProps {
  label: string;
  checked: boolean;
  disabled?: boolean;
  onChange?: ChangeEventHandler<HTMLInputElement>;
}

function Checkbox({
  label,
  checked,
  disabled = false,
  onChange,
}: CheckboxProps) {
  return (
    <S_Label $disabled={disabled}>
      <S_Input
        type="checkbox"
        checked={checked}
        disabled={disabled}
        onChange={onChange}
      />
      <S_Indicator checked={checked} $disabled={disabled} />
      <S_Text $disabled={disabled}>{label}</S_Text>
    </S_Label>
  );
}

export default Checkbox;

const S_Label = styled.label<{ $disabled: boolean }>`
  display: inline-flex;
  align-items: center;
  gap: 0.8rem;

  width: fit-content;
  cursor: ${({ $disabled }) => ($disabled ? 'not-allowed' : 'pointer')};
`;

const S_Input = styled.input`
  position: absolute;

  width: 1px;
  height: 1px;
  opacity: 0;
  pointer-events: none;
`;

const S_Indicator = styled.span<{ checked: boolean; $disabled: boolean }>`
  display: inline-flex;
  align-items: center;
  justify-content: center;

  ${({ theme, checked, $disabled }) => {
    if ($disabled) {
      return css`
        border: 1px solid ${theme.SYSTEM.GRAY300};

        background-color: ${theme.SYSTEM.GRAY50};

        &::after {
          border-right: 2px solid ${theme.SYSTEM.GRAY400};
          border-bottom: 2px solid ${theme.SYSTEM.GRAY400};
        }
      `;
    }

    if (checked) {
      return css`
        border: 1px solid ${theme.SYSTEM.MAIN500};

        background-color: ${theme.SYSTEM.MAIN500};

        &::after {
          border-right: 2px solid ${theme.BG.WHITE};
          border-bottom: 2px solid ${theme.BG.WHITE};
        }
      `;
    }

    return css`
      border: 1px solid ${theme.OUTLINE.DARK};

      background-color: ${theme.BG.WHITE};

      &::after {
        border-right: 2px solid ${theme.BG.WHITE};
        border-bottom: 2px solid ${theme.BG.WHITE};
      }
    `;
  }}

  width: 1.8rem;
  height: 1.8rem;
  border-radius: 0.4rem;

  &::after {
    content: '';

    width: 0.5rem;
    height: 0.9rem;
    opacity: ${({ checked }) => (checked ? 1 : 0)};
    transform: rotate(45deg) translate(-1px, -1px);
  }
`;

const S_Text = styled.span<{ $disabled: boolean }>`
  color: ${({ theme, $disabled }) =>
    $disabled ? theme.FONT.G01 : theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
`;
