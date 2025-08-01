import { useState } from 'react';

import { validateLength } from '../../../common/utils/validateLength';
import { ERROR_MESSAGE } from '../constants/errorMessage';
import { PASSWORD } from '../constants/password';

const usePassword = () => {
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  const handlePasswordConfirmChange = (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    setPasswordConfirm(e.target.value);
  };

  const getPasswordErrorMessage = () => {
    if (password.length === 0 || passwordConfirm.length === 0) {
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

  const getPasswordConfirmError = () => {
    if (password.length === 0 || passwordConfirm.length === 0) {
      return '';
    }

    if (password !== passwordConfirm) {
      return ERROR_MESSAGE.NOT_MATCH;
    }

    return '';
  };

  return {
    password,
    passwordConfirm,
    handlePasswordChange,
    handlePasswordConfirmChange,
    passwordErrorMessage: getPasswordErrorMessage(),
    passwordConfirmErrorMessage: getPasswordConfirmError(),
  };
};

export default usePassword;
