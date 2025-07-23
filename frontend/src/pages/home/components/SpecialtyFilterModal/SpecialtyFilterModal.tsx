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
