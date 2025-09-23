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
    <S_Container>
      <S_HiddenCheckbox
        type="checkbox"
        checked={checked}
        onChange={onChange}
        disabled={disabled}
      />
      <S_CheckboxLabel checked={checked} disabled={disabled}>
        {specialty}
      </S_CheckboxLabel>
    </S_Container>
  );
}

export default SpecialtyCheckbox;

const S_Container = styled.label`
  display: inline-flex;
  align-items: center;

  transition: all 0.2s ease;
  user-select: none;
`;

const S_HiddenCheckbox = styled.input`
  position: absolute;

  width: 0;
  height: 0;
  opacity: 0;
`;

const S_CheckboxLabel = styled.span<{
  checked: boolean;
  disabled: boolean;
}>`
  display: inline-flex;
  align-items: center;

  padding: 0.6rem 1.2rem;
  border: 1px solid
    ${({ theme, checked }) =>
      checked ? theme.SYSTEM.MAIN500 : theme.OUTLINE.DARK};
  border-radius: 16px;

  color: ${({ theme, checked }) =>
    checked ? theme.SYSTEM.MAIN500 : theme.FONT.B02};

  ${({ theme }) => theme.TYPOGRAPHY.B4_R};

  transition: all 0.2s ease;

  cursor: ${({ disabled }) => (disabled ? 'not-allowed' : 'pointer')};
  opacity: ${({ disabled }) => (disabled ? 0.5 : 1)};

  &:hover {
    border-color: ${({ theme, disabled }) =>
      disabled ? theme.OUTLINE.DARK : theme.SYSTEM.MAIN500};
  }

  &:active {
    transform: scale(0.98);
  }
`;
