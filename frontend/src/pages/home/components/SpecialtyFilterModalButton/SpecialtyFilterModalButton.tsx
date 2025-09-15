import styled from '@emotion/styled';

import goIcon from '../../../../common/assets/images/goIcon.svg';

interface SpecialtyFilterModalButtonProps {
  handleOpenModal: () => void;
}

function SpecialtyFilterModalButton({
  handleOpenModal,
}: SpecialtyFilterModalButtonProps) {
  return (
    <StyledButton onClick={handleOpenModal} type="button">
      <StyledGoIcon src={goIcon} alt="카테고리 열기 아이콘" />
      <StyledText>카테고리</StyledText>
    </StyledButton>
  );
}

export default SpecialtyFilterModalButton;

const StyledButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: space-between;

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
