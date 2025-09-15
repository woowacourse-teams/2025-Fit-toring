import styled from '@emotion/styled';

import downIcon from '../../../../common/assets/images/downIcon.svg';
interface SortButtonProps {
  handleSortButtonClick: () => void;
}

function SortButton({ handleSortButtonClick }: SortButtonProps) {
  return (
    <StyledButton onClick={handleSortButtonClick} type="button">
      <StyledText>기본순</StyledText>
      <StyledGoIcon src={downIcon} alt="정렬 아이콘" />
    </StyledButton>
  );
}

export default SortButton;

const StyledButton = styled.button`
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

const StyledText = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.C4_R};
  color: ${({ theme }) => theme.SYSTEM.GRAY600};
`;

const StyledGoIcon = styled.img`
  width: 1.4rem;
  aspect-ratio: 1 / 1;
`;
