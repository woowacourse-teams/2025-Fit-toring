import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import { getSpecialties } from '../../../apis/getSpecialties';
import SpecialtyTag from '../SpecialtyTag/SpecialtyTag';
import TitleSeparator from '../TitleSeparator/TitleSeparator';

import type { mentoringCreateFormData } from '../../../types/mentoringCreateFormData';
import type { Specialty } from '../../../types/Specialty';
import { captureSentryError } from '../../../utils/captureSentryError';

const MAX_SPECIALTIES = 3;

interface SpecialtySectionProps {
  onSpecialtyChange: (
    newData: Pick<mentoringCreateFormData, 'category'>,
  ) => void;
  initialSelectedSpecialties?: string[];
}

function SpecialtySection({
  initialSelectedSpecialties,
  onSpecialtyChange,
}: SpecialtySectionProps) {
  const [specialties, setSpecialties] = useState<Specialty[]>([]);
  const [selectedSpecialties, setSelectedSpecialties] = useState<string[]>(
    initialSelectedSpecialties ?? [],
  );

  const handleToggleSpecialtyTagChange = (title: string) => {
    setSelectedSpecialties((prev) => {
      const next = prev.includes(title)
        ? prev.filter((item) => item !== title)
        : [...prev, title];
      onSpecialtyChange({ category: next });
      return next;
    });
  };

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
          feature: 'mentoring',
          step: 'specialty-fetch',
        });
      }
    };

    fetchSpecialties();
  }, []);

  return (
    <section>
      <TitleSeparator>전문 분야</TitleSeparator>

      <StyledGuideText>
        최대 {MAX_SPECIALTIES}개까지 등록 가능합니다.
      </StyledGuideText>
      <StyledSpecialtyWrapper>
        {specialties.map((specialty) => (
          <SpecialtyTag
            key={specialty.id}
            title={specialty.title}
            onChange={() => handleToggleSpecialtyTagChange(specialty.title)}
            disabled={
              selectedSpecialties.length >= MAX_SPECIALTIES &&
              !selectedSpecialties.includes(specialty.title)
            }
            checked={selectedSpecialties.includes(specialty.title)}
          />
        ))}
      </StyledSpecialtyWrapper>
    </section>
  );
}

export default SpecialtySection;

const StyledSpecialtyWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 1.2rem;

  width: 100%;
  height: 100%;
`;

const StyledGuideText = styled.p`
  margin-bottom: 2rem;
  padding-left: 0.5rem;

  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
  color: ${({ theme }) => theme.FONT.B04}
`;
