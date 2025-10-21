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
    <S_Container checked={checked} disabled={disabled}>
      <S_HiddenCheckbox
        type="checkbox"
        checked={checked}
        onChange={onChange}
        disabled={disabled}
      />
      {specialty}
    </S_Container>
  );
}

export default SpecialtyCheckbox;

const S_Container = styled.label<{
  checked: boolean;
  disabled: boolean;
}>`
  display: inline-flex;
  align-items: center;
  position: relative;

  padding: 0.6rem 1.2rem;
  border: 1px solid
    ${({ theme, checked }) =>
      checked ? theme.SYSTEM.MAIN500 : theme.OUTLINE.DARK};
  border-radius: 16px;
  user-select: none;

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

  &:focus-within {
    border-color: ${({ theme }) => theme.OUTLINE.BLACK};
  }
`;

const S_HiddenCheckbox = styled.input`
  overflow: hidden;
  position: absolute;

  width: 1px;
  height: 1px;
  margin: -1px;
  padding: 0;
  border: 0;
  clip: rect(0, 0, 0, 0);
`;
