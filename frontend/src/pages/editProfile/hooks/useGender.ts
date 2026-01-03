import { useState } from 'react';

import type { GenderClient } from '../../../common/types/gender';
import type { SignupInfo } from '../../signup/types/signupInfo';

const useGender = (initialGender?: GenderClient) => {
  const [gender, setGender] = useState<GenderClient>(initialGender || '남');

  const handleGenderChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;

    const isGenderType = (value: string): value is SignupInfo['gender'] => {
      const genders = ['남', '여'];
      return genders.includes(value);
    };

    if (!isGenderType(value)) {
      return;
    }

    setGender(value);
  };

  return {
    gender,
    handleGenderChange,
  };
};

export default useGender;
