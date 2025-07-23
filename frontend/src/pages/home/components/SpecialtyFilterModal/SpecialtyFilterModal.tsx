import styled from '@emotion/styled';

import Modal from '../../../../common/components/Modal/Modal';

interface SpecialtyModalProps {
  opened: boolean;
  handleCloseModal: () => void;

  selectedSpecialties: string[];
  handleReset: () => void;
  handleApply: () => void;
}

function SpecialtyModal({
  opened,
  handleCloseModal,

  selectedSpecialties,
  handleReset,
  handleApply,
}: SpecialtyModalProps) {
  return (
    <Modal opened={opened} onCloseClick={handleCloseModal}>
      <StyledContainer>
        <StyledTitle>전문 분야</StyledTitle>
        <StyledLine />
      </StyledContainer>
    </Modal>
  );
}

export default SpecialtyModal;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;

  align-items: center;
  justify-content: center;

  gap: 1rem;
`;

const StyledTitle = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R};
`;

const StyledLine = styled.hr`
  width: 100%;
  height: 1px;
  margin: 0;
  border: none;
  border-top: 1px solid ${({ theme }) => theme.BORDER.GRAY300};
`;
