import styled from '@emotion/styled';

interface SpecialtyCheckboxProps {
  specialty: string;
  checked: boolean;
  disabled: boolean;
  onChange: () => void;
}

function SpecialtyCheckbox({
  specialty,
  checked,
  disabled,
  onChange,
}: SpecialtyCheckboxProps) {
  return (
    <StyledContainer>
      <StyledHiddenCheckbox
        type="checkbox"
        checked={checked}
        onChange={onChange}
        disabled={disabled}
      />
      <StyledCheckboxLabel checked={checked} disabled={disabled}>
        {specialty}
      </StyledCheckboxLabel>
    </StyledContainer>
  );
}

export default SpecialtyCheckbox;

const StyledContainer = styled.label`
  display: inline-flex;
  align-items: center;

  transition: all 0.2s ease;
  user-select: none;
`;

const StyledHiddenCheckbox = styled.input`
  position: absolute;

  width: 0;
  height: 0;
  opacity: 0;
`;

const StyledCheckboxLabel = styled.span<{
  checked: boolean;
  disabled: boolean;
}>`
  display: inline-flex;
  align-items: center;

  padding: 0.6rem 1.2rem;
  border: 1px solid
    ${({ theme, checked }) =>
      checked ? theme.SYSTEM.MAIN800 : theme.OUTLINE.DARK};
  border-radius: 16px;

  background-color: ${({ theme, checked }) =>
    checked ? theme.SYSTEM.MAIN600 : theme.BG.WHITE};

  color: ${({ theme, checked }) => (checked ? theme.BG.WHITE : theme.FONT.B02)};

  ${({ theme }) => theme.TYPOGRAPHY.B4_R};

  transition: all 0.2s ease;

  cursor: ${({ disabled }) => (disabled ? 'not-allowed' : 'pointer')};
  opacity: ${({ disabled }) => (disabled ? 0.5 : 1)};

  &:hover {
    border-color: ${({ theme, disabled }) =>
      disabled ? theme.OUTLINE.DARK : theme.SYSTEM.MAIN500};

    background-color: ${({ theme, checked }) =>
      checked ? theme.SYSTEM.MAIN500 : theme.BG.WHITE};
  }

  &:active {
    transform: scale(0.98);
  }
`;
