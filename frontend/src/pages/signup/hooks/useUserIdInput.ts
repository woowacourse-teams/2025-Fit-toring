import { useState } from 'react';

import { validateLength } from '../../../common/utils/validateLength';
import { ERROR_MESSAGE } from '../constants/errorMessage';
import { ID } from '../constants/id';

const useUserIdInput = () => {
  const [userId, setUserIdId] = useState('');

  const handleUserIdChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUserIdId(e.target.value);
  };

  const getUserIdErrorMessage = () => {
    if (userId.length === 0) {
      return '';
    }

    const regex = /^[a-zA-Z0-9]+$/; // 영문과 숫자만 허용

    if (!regex.test(userId)) {
      return ERROR_MESSAGE.INVALID_USER_ID_CHARACTERS;
    }

    if (
      !validateLength({
        min: ID.MIN_LENGTH,
        max: ID.MAX_LENGTH,
        value: userId,
      })
    ) {
      return ERROR_MESSAGE.INVALID_ID_LENGTH;
    }

    return '';
  };

  return {
    userId,
    errorMessage: getUserIdErrorMessage(),
    handleUserIdChange,
  };
};

export default useUserIdInput;
