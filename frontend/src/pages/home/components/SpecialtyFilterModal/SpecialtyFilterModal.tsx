import { useState } from 'react';

import styled from '@emotion/styled';

import Modal from '../../../../common/components/Modal/Modal';
import SpecialtyCheckbox from '../SpecialtyCheckbox/SpecialtyCheckbox';

const ALL_SPECIALTIES = [
  { id: 'weightLoss', label: '체중 감량' },
  { id: 'muscleGain', label: '근력 증진' },
  { id: 'bodyCorrection', label: '체형 교정' },
  { id: 'muscleExercise', label: '근력 운동' },
  { id: 'bulkUp', label: '벌크업' },
  { id: 'rehabExercise', label: '재활 운동' },
  { id: 'nutritionCounseling', label: '영양 상담' },
  { id: 'postureCorrection', label: '자세 교정' },
  { id: 'stretching', label: '스트레칭' },
  { id: 'diet', label: '다이어트' },
  { id: 'competitionPrep', label: '대회 준비' },
] as const;

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
  const [temporarySelectedSpecialties, setTemporarySelectedSpecialties] =
    useState<string[]>(selectedSpecialties);

  if (!opened) return null;

  const handleTemporarySpecialtyToggle = (specialtyId: string) => {
    setTemporarySelectedSpecialties((prev) =>
      prev.includes(specialtyId)
        ? prev.filter((id) => id !== specialtyId)
        : [...prev, specialtyId],
    );
  };

  return (
    <Modal opened={opened} onCloseClick={handleCloseModal}>
      <StyledContainer>
        <StyledTitle>전문 분야</StyledTitle>
        <StyledLine />

        <StyledSpecialtyWrapper>
          {ALL_SPECIALTIES.map((specialty) => (
            <SpecialtyCheckbox
              key={specialty.id}
              specialty={specialty.label}
              checked={temporarySelectedSpecialties.includes(specialty.id)}
              onChange={() => handleTemporarySpecialtyToggle(specialty.id)}
            />
          ))}
        </StyledSpecialtyWrapper>

        <StyledLine />
        <StyledButtonWrapper>
          <StyledSecondaryButton onClick={handleReset}>
            초기화
          </StyledSecondaryButton>
          <StyledPrimaryButton onClick={handleApply}>적용</StyledPrimaryButton>
        </StyledButtonWrapper>
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

const StyledSpecialtyWrapper = styled.div`
  display: flex;
  padding: 0.4rem;
  overflow-y: auto;
  flex-wrap: wrap;
  gap: 0.8rem;
  justify-content: center;

  max-height: 15rem;
`;

const StyledButtonWrapper = styled.div`
  display: flex;
  width: 100%;
  padding: 0.4rem;

  gap: 1.2rem;
`;

const StyledButton = styled.button`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  width: 100%;
  padding: 1.2rem 1.6rem;

  ${({ theme }) => theme.TYPOGRAPHY.BTN2_R};

  transition: all 0.2s ease;
  cursor: pointer;
`;

const StyledPrimaryButton = styled(StyledButton)`
  border: 1px solid ${({ theme }) => theme.SYSTEM.MAIN800};

  background-color: ${({ theme }) => theme.SYSTEM.MAIN800};

  color: ${({ theme }) => theme.BG.WHITE};
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 10%);

  &:hover {
    background-color: ${({ theme }) => theme.SYSTEM.MAIN700};
  }
`;

const StyledSecondaryButton = styled(StyledButton)`
  border: 1px solid ${({ theme }) => theme.BORDER.GRAY300};

  background-color: transparent;

  color: ${({ theme }) => theme.FONT.B01};

  &:hover {
    background-color: ${({ theme }) => theme.BG.LIGHT};
  }
`;
