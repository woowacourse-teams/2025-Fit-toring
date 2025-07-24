import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import Modal from '../../../../common/components/Modal/Modal';
import { getSpecialties } from '../../apis/getSpecialties';
import SpecialtyCheckbox from '../SpecialtyCheckbox/SpecialtyCheckbox';

import type { Specialty } from '../../types/Specialty';

const MAX_SPECIALTIES = 3;

interface SpecialtyModalProps {
  opened: boolean;
  handleCloseModal: () => void;

  selectedSpecialties: string[];

  handleApply: (specialties: string[]) => void;
}

function SpecialtyModal({
  opened,
  handleCloseModal,

  selectedSpecialties,
  handleApply,
}: SpecialtyModalProps) {
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

  if (!opened) return null;

  const handleToggleTemporarySpecialty = (specialty: string) => {
    setTemporarySelectedSpecialties((prev) =>
      prev.includes(specialty)
        ? prev.filter((prevSpecialty) => prevSpecialty !== specialty)
        : [...prev, specialty],
    );
  };

  const handleResetTemporarySpecialties = () => {
    setTemporarySelectedSpecialties([]);
  };
  return (
    <Modal opened={opened} onCloseClick={handleCloseModal}>
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
