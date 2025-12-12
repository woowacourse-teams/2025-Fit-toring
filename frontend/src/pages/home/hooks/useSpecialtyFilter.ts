import { useState } from 'react';

import type { Specialty } from '../../../common/types/Specialty';

const useSpecialtyFilter = () => {
  const [selectedSpecialties, setSelectedSpecialties] = useState<Specialty[]>(
    [],
  );

  const applySpecialties = (specialties: Specialty[]) => {
    setSelectedSpecialties(specialties);
  };

  const toggleSpecialty = (specialty: Specialty) => {
    setSelectedSpecialties((prev) => {
      const hasSpecialty = prev.find(
        (prevSpecialty) => prevSpecialty.id === specialty.id,
      );
      return hasSpecialty
        ? prev.filter((prevSpecialty) => prevSpecialty.id !== specialty.id)
        : [...prev, specialty];
    });
  };

  return {
    selectedSpecialties,
    applySpecialties,
    toggleSpecialty,
  };
};

export default useSpecialtyFilter;
