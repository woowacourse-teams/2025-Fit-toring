import styled from '@emotion/styled';

interface CheckboxProps {
  checked: boolean;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  label?: React.ReactElement;
  errored?: boolean;
  id: string;
}

function Checkbox({ checked, onChange, label, errored, id }: CheckboxProps) {
  return (
    <StyledContainer>
      <HiddenInput
        type="checkbox"
        checked={checked}
        onChange={onChange}
        id={id}
      />
      <CustomCheckbox errored={errored}>{checked && '✔'}</CustomCheckbox>
      {label}
    </StyledContainer>
  );
}

const StyledContainer = styled.label`
  display: flex;
  align-items: center;
  gap: 0.7rem;
  cursor: pointer;
`;

const HiddenInput = styled.input`
  display: none;
`;

const CustomCheckbox = styled.div<{ errored?: boolean }>`
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;

  width: 1.8rem;
  height: 1.8rem;
  border: 2px solid
    ${({ theme, errored }) =>
      errored ? theme.FONT.ERROR : theme.SYSTEM.MAIN600};
  border-radius: 3px;

  background-color: ${({ children, theme }) =>
    children ? theme.SYSTEM.MAIN600 : 'transparent'};

  color: white;
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
`;

export default Checkbox;
