import styled from '@emotion/styled';

import goIcon from '../../../../common/assets/images/goIcon.svg';

interface SpecialtiesModalButtonProps {
  handleOpenModal: () => void;
}

function SpecialtiesModalButton({
  handleOpenModal,
}: SpecialtiesModalButtonProps) {
  return (
    <StyledButton onClick={handleOpenModal} type="button">
      <StyledText>전문 분야</StyledText>
      <StyledGoIcon src={goIcon} alt="모달 열기 아이콘" />
    </StyledButton>
  );
}

export default SpecialtiesModalButton;

const StyledButton = styled.button`
  display: inline-flex;
  width: 100%;
  padding: 0.7rem 1.1rem;
  border: 1px solid ${({ theme }) => theme.BORDER.GRAY300};
  justify-content: space-between;
  align-items: center;

  border-radius: 6.75px;
  background: ${({ theme }) => theme.BG.WHITE};
  cursor: pointer;
`;

const StyledText = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.BTN4_R};
`;

const StyledGoIcon = styled.img`
  width: 1.4rem;
  aspect-ratio: 1 / 1;
`;
