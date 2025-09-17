import styled from '@emotion/styled';

interface SpecialtyTagProps {
  title: string;
  disabled: boolean;
  checked: boolean;
  onChange: () => void;
}

type LabelType = Pick<SpecialtyTagProps, 'disabled' | 'checked'>;

function SpecialtyTag({
  title,
  disabled,
  checked,
  onChange,
}: SpecialtyTagProps) {
  return (
    <S_Container checked={checked} disabled={disabled}>
      <S_HiddenInput onChange={onChange} type="checkbox" disabled={disabled} />
      {title}
    </S_Container>
  );
}

export default SpecialtyTag;

const S_Container = styled.label<LabelType>`
  display: flex;
  align-items: center;
  justify-content: center;

  width: fit-content;
  height: 3.2rem;
  padding: 0.9rem 1.3rem;
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
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};

    background-color: ${({ theme, checked }) =>
      checked ? theme.SYSTEM.MAIN500 : theme.BG.LIGHT};
  }

  &:active {
    transform: scale(0.98);
  }
`;

const S_HiddenInput = styled.input`
  display: none;
`;
