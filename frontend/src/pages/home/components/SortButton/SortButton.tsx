import styled from '@emotion/styled';

import downIcon from '../../../../common/assets/images/downIcon.svg';
interface SortButtonProps {
  handleSortButtonClick: () => void;
}

function SortButton({ handleSortButtonClick }: SortButtonProps) {
  return (
    <S_Button onClick={handleSortButtonClick} type="button">
      <S_Text>기본순</S_Text>
      <S_GoIcon src={downIcon} alt="정렬 아이콘" />
    </S_Button>
  );
}

export default SortButton;

const S_Button = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.6rem;

  width: 9.4rem;
  height: 3.4rem;
  padding: 1rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-radius: 6.75px;

  background: ${({ theme }) => theme.BG.WHITE};
  cursor: pointer;
`;

const S_Text = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.C4_R};
  color: ${({ theme }) => theme.SYSTEM.GRAY600};
`;

const S_GoIcon = styled.img`
  width: 1.4rem;
  aspect-ratio: 1 / 1;
`;
