import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import { getSpecialties } from '../../../../common/apis/getSpecialties';
import Modal from '../../../../common/components/Modal/Modal';
import SpecialtyCheckbox from '../SpecialtyCheckbox/SpecialtyCheckbox';

import type { Specialty } from '../../../../common/types/Specialty';

const MAX_SPECIALTIES = 3;

interface SpecialtyFilterModalProps {
  opened: boolean;
  handleCloseModal: () => void;

  selectedSpecialties: string[];
  handleApplyFinalSpecialties: (specialties: string[]) => void;
}

function SpecialtyFilterModal({
  opened,
  handleCloseModal,

  selectedSpecialties,
  handleApplyFinalSpecialties,
}: SpecialtyFilterModalProps) {
  const [specialties, setSpecialties] = useState<Specialty[]>([]);

  useEffect(() => {
    const fetchSpecialties = async () => {
      try {
        const data = await getSpecialties();
        setSpecialties(data);
      } catch (error) {
        console.error('전문 분야 가져오기 실패:', error);
      }
    };

    fetchSpecialties();
  }, []);

  const [temporarySelectedSpecialties, setTemporarySelectedSpecialties] =
    useState<string[]>(selectedSpecialties);

  useEffect(() => {
    setTemporarySelectedSpecialties(selectedSpecialties);
  }, [selectedSpecialties]);

  if (!opened) {
    return null;
  }

  const handleToggleTemporarySpecialty = (specialty: string) => {
    setTemporarySelectedSpecialties((prev) =>
      prev.includes(specialty)
        ? prev.filter((prevSpecialty) => prevSpecialty !== specialty)
        : [...prev, specialty],
    );
  };

  const handleApplySpecialties = () => {
    handleApplyFinalSpecialties(temporarySelectedSpecialties);
  };

  const handleResetTemporarySpecialties = () => {
    setTemporarySelectedSpecialties([]);
  };

  const handleRollbackTemporarySpecialties = () => {
    setTemporarySelectedSpecialties(selectedSpecialties);
    handleCloseModal();
  };

  return (
    <Modal opened={opened} onCloseClick={handleRollbackTemporarySpecialties}>
      <StyledContainer>
        <StyledTitle>전문 분야</StyledTitle>
        <StyledLine />

        <StyledSpecialtyWrapper>
          {specialties.map((specialty) => (
            <SpecialtyCheckbox
              key={specialty.id}
              specialty={specialty.title}
              checked={temporarySelectedSpecialties.includes(specialty.title)}
              disabled={
                temporarySelectedSpecialties.length >= MAX_SPECIALTIES &&
                !temporarySelectedSpecialties.includes(specialty.title)
              }
              onChange={() => handleToggleTemporarySpecialty(specialty.title)}
            />
          ))}
        </StyledSpecialtyWrapper>

        <StyledLine />
        <StyledButtonWrapper>
          <StyledSecondaryButton onClick={handleResetTemporarySpecialties}>
            초기화
          </StyledSecondaryButton>
          <StyledPrimaryButton onClick={handleApplySpecialties}>
            적용
          </StyledPrimaryButton>
        </StyledButtonWrapper>
      </StyledContainer>
    </Modal>
  );
}

export default SpecialtyFilterModal;

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
  border-top: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const StyledSpecialtyWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.8rem;

  max-height: 15rem;
  padding: 0.4rem;
  overflow-y: auto;
`;

const StyledButtonWrapper = styled.div`
  display: flex;
  gap: 1.2rem;

  width: 100%;
  padding: 0.4rem;
`;

const StyledButton = styled.button`
  display: inline-flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  padding: 1.2rem 1.6rem;
  border-radius: 6px;

  ${({ theme }) => theme.TYPOGRAPHY.BTN2_R};

  transition: all 0.2s ease;
  cursor: pointer;
`;

const StyledPrimaryButton = styled(StyledButton)`
  border: 1px solid ${({ theme }) => theme.SYSTEM.MAIN600};
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.SYSTEM.MAIN600};

  color: ${({ theme }) => theme.BG.WHITE};

  &:hover {
    background-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }
`;

const StyledSecondaryButton = styled(StyledButton)`
  border: 1px solid ${({ theme }) => theme.OUTLINE.DARK};

  background-color: transparent;

  color: ${({ theme }) => theme.FONT.B02};

  &:hover {
    background-color: ${({ theme }) => theme.BG.LIGHT};
  }
`;
