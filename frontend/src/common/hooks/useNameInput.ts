import { useState } from 'react';

import { ERROR_MESSAGE } from '../../pages/booking/constants/errorMessage';

const useNameInput = () => {
  const [name, setName] = useState('');

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setName(e.target.value);
  };

  const getNameErrorMessage = () => {
    if (name === '') {
      return '';
    }

    if (name.length < 1 || name.length > 5) {
      return ERROR_MESSAGE.INVALID_NAME_LENGTH;
    }

    const regex = /^[ㄱ-ㅎ가-힣]+$/; // 한글만 허용

    if (!regex.test(name)) {
      return ERROR_MESSAGE.INVALID_NAME_CHARACTERS;
    }

    return '';
  };

  const errorMessage = getNameErrorMessage();

  return { name, handleNameChange, errorMessage };
};

export default useNameInput;
