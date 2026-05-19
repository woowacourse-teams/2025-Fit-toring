import styled from '@emotion/styled';

interface MyPageMenuRowProps {
  iconSrc: string;
  label: string;
  onClick: () => void;
}

function MyPageMenuRow({ iconSrc, label, onClick }: MyPageMenuRowProps) {
  return (
    <S_Button onClick={onClick} type="button">
      <S_Label>{label}</S_Label>
      <S_Icon src={iconSrc} alt="" aria-hidden="true" />
    </S_Button>
  );
}

export default MyPageMenuRow;

const S_Button = styled.button`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.2rem;

  width: 100%;
  padding: 0;
  border: none;

  background: transparent;
  appearance: none;
  cursor: pointer;

  font: inherit;
  text-align: left;
`;

const S_Label = styled.span`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H4_R}
`;

const S_Icon = styled.img`
  flex-shrink: 0;

  width: 0.8rem;
  height: auto;
`;
