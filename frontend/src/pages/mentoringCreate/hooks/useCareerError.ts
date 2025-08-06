import { useEffect, useState } from 'react';

import { careerValidator } from '../utils/careerValidator';

export const useCareerError = (career: number | null) => {
  const [careerErrorMessage, setCareerErrorMessage] = useState<string>('');

  useEffect(() => {
    setCareerErrorMessage(careerValidator(career));
  }, [career]);

  return { careerErrorMessage };
};
