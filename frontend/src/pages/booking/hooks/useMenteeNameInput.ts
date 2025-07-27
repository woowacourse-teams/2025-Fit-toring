import { useState } from 'react';

import { ERROR_MESSAGE } from '../constants/errorMessage';

const useMenteeNameInput = () => {
  const [menteeName, setMenteeName] = useState('');

  const handleMenteeNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setMenteeName(e.target.value);
  };

  const getMenteeNameErrorMessage = () => {
    if (menteeName === '') return '';

    if (menteeName.length < 1 || menteeName.length > 5) {
      return ERROR_MESSAGE.INVALID_MENTEE_NAME_LENGTH;
    }

    const regex = /^[ㄱ-ㅎ가-힣0-9]+$/; // 한글만 허용

    if (!regex.test(menteeName)) {
      return ERROR_MESSAGE.INVALID_MENTEE_NAME_CHARACTERS;
    }

    return '';
  };

  const errorMessage = getMenteeNameErrorMessage();

  return { menteeName, handleMenteeNameChange, errorMessage };
};

export default useMenteeNameInput;
