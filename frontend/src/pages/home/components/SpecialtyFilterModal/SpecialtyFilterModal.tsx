import { useEffect, useState } from 'react';

import styled from '@emotion/styled';
import ReactGA from 'react-ga4';

import { getSpecialties } from '../../../../common/apis/getSpecialties';
import Modal from '../../../../common/components/Modal/Modal';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import SpecialtyCheckbox from '../SpecialtyCheckbox/SpecialtyCheckbox';

import type { Specialty } from '../../../../common/types/Specialty';

const MAX_SPECIALTIES = 3;

interface SpecialtyFilterModalProps {
  opened: boolean;
  handleCloseModal: () => void;

  selectedSpecialties: Specialty[];
  handleApplyFinalSpecialties: (specialties: Specialty[]) => void;
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
        captureSentryError({
          error,
          level: 'warning',
          feature: 'home',
          step: 'specialty-fetch',
        });
      }
    };

    fetchSpecialties();
  }, []);

  const [temporarySelectedSpecialties, setTemporarySelectedSpecialties] =
    useState<Specialty[]>(selectedSpecialties);

  useEffect(() => {
    setTemporarySelectedSpecialties(selectedSpecialties);
  }, [selectedSpecialties]);

  if (!opened) {
    return null;
  }

  const handleToggleTemporarySpecialty = (specialty: Specialty) => {
    setTemporarySelectedSpecialties((prev) => {
      const hasSpecialty = prev.find(
        (prevSpecialty) => prevSpecialty.id === specialty.id,
      );
      return hasSpecialty
        ? prev.filter((prevSpecialty) => prevSpecialty.id !== specialty.id)
        : [...prev, specialty];
    });
  };

  const handleApplySpecialties = () => {
    handleApplyFinalSpecialties(temporarySelectedSpecialties);
    ReactGA.event({
      category: 'Specialty Filter',
      action: 'Apply Specialty Filter',
      label: '전문 분야 필터 적용',
    });
  };

  const handleResetTemporarySpecialties = () => {
    setTemporarySelectedSpecialties([]);
    ReactGA.event({
      category: 'Specialty Filter',
      action: 'Reset Specialty Filter',
      label: '전문 분야 필터 초기화',
    });
  };

  const handleRollbackTemporarySpecialties = () => {
    setTemporarySelectedSpecialties(selectedSpecialties);
    handleCloseModal();
  };

  return (
    <Modal opened={opened} onCloseClick={handleRollbackTemporarySpecialties}>
      <S_Container>
        <S_Title>전문 분야 (최대 {MAX_SPECIALTIES}개)</S_Title>
        <S_VisuallyHidden>
          총 {specialties.length}개의 선택지가 있습니다.
        </S_VisuallyHidden>
        <S_Line />

        <S_SpecialtyWrapper>
          <S_VisuallyHidden role="status">
            {temporarySelectedSpecialties.length > 0 &&
              `현재 ${temporarySelectedSpecialties.length}개`}
            {temporarySelectedSpecialties.length >= MAX_SPECIALTIES &&
              `, 최대 개수 도달`}
          </S_VisuallyHidden>
          {specialties.map((specialty) => (
            <SpecialtyCheckbox
              key={specialty.id}
              specialty={specialty.title}
              checked={
                !!temporarySelectedSpecialties.find(
                  (s) => s.id === specialty.id,
                )
              }
              disabled={
                temporarySelectedSpecialties.length >= MAX_SPECIALTIES &&
                !temporarySelectedSpecialties.find((s) => s.id === specialty.id)
              }
              onChange={() => handleToggleTemporarySpecialty(specialty)}
            />
          ))}
        </S_SpecialtyWrapper>

        <S_Line />
        <S_ButtonWrapper>
          <S_VisuallyHidden>
            <h4>전문 분야 필터 모달 버튼</h4>
          </S_VisuallyHidden>
          <S_SecondaryButton
            onClick={handleResetTemporarySpecialties}
            aria-label="선택한 전문 분야 초기화"
          >
            초기화
          </S_SecondaryButton>
          <S_PrimaryButton
            onClick={handleApplySpecialties}
            aria-label={`${temporarySelectedSpecialties.length}개의 전문 분야 적용 및 닫기`}
          >
            적용
          </S_PrimaryButton>
        </S_ButtonWrapper>
      </S_Container>
    </Modal>
  );
}

export default SpecialtyFilterModal;

const S_Container = styled.article`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem;
`;

const S_Title = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R};
`;

const S_Line = styled.hr`
  width: 100%;
  height: 1px;
  margin: 0;
  border: none;
  border-top: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_SpecialtyWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.8rem;

  max-height: 15rem;
  padding: 0.4rem;
  overflow-y: auto;
`;

const S_ButtonWrapper = styled.div`
  display: flex;
  gap: 1.2rem;

  width: 100%;
  padding: 0.4rem;
`;

const S_Button = styled.button`
  display: inline-flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  padding: 1.2rem 1.6rem;
  border-radius: 6px;

  ${({ theme }) => theme.TYPOGRAPHY.BTN2_R};

  transition: all 0.2s ease;
  cursor: pointer;

  &:hover {
    scale: 1.05;
  }
`;

const S_PrimaryButton = styled(S_Button)`
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY900};
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.SYSTEM.GRAY900};

  color: ${({ theme }) => theme.BG.WHITE};
`;

const S_SecondaryButton = styled(S_Button)`
  border: 1px solid ${({ theme }) => theme.OUTLINE.DARK};

  background-color: transparent;

  color: ${({ theme }) => theme.FONT.B02};

  &:hover {
    background-color: ${({ theme }) => theme.BG.LIGHT};
  }
`;

const S_VisuallyHidden = styled.div`
  overflow: hidden;
  position: absolute;

  width: 1px;
  height: 1px;
  margin: -1px;
  padding: 0;
  border: 0;

  white-space: nowrap;
  clip: rect(0, 0, 0, 0);
`;
