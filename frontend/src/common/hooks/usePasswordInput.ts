import { useState } from 'react';

import { ERROR_MESSAGE } from '../constants/errorMessage';
import { PASSWORD } from '../constants/password';
import { validateLength } from '../utils/validateLength';

const usePasswordInput = () => {
  const [password, setPassword] = useState('');

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  const getPasswordErrorMessage = () => {
    if (password.length === 0) {
      return '';
    }

    if (
      !validateLength({
        min: PASSWORD.MIN_LENGTH,
        max: PASSWORD.MAX_LENGTH,
        value: password,
      })
    ) {
      return ERROR_MESSAGE.INVALID_PASSWORD_LENGTH;
    }

    return '';
  };

  const passwordErrorMessage = getPasswordErrorMessage();

  return {
    password,
    handlePasswordChange,
    passwordErrorMessage,
    passwordValidated: password !== '' && passwordErrorMessage === '',
  };
};

export default usePasswordInput;
