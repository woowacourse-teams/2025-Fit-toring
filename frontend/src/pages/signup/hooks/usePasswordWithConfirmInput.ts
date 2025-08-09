import { useState } from 'react';

import { ERROR_MESSAGE } from '../../../common/constants/errorMessage';
import usePasswordInput from '../../../common/hooks/usePasswordInput';

const usePasswordWithConfirmInput = () => {
  const { password, passwordErrorMessage, handlePasswordChange } =
    usePasswordInput();
  const [passwordConfirm, setPasswordConfirm] = useState('');

  const handlePasswordConfirmChange = (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    setPasswordConfirm(e.target.value);
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

  const passwordConfirmErrorMessage = getPasswordConfirmError();

  return {
    password,
    passwordConfirm,
    handlePasswordChange,
    handlePasswordConfirmChange,
    passwordErrorMessage,
    passwordConfirmErrorMessage,
    passwordValidated: password !== '' && passwordErrorMessage === '',
    passwordConfrimValidated:
      passwordConfirm !== '' && passwordConfirmErrorMessage === '',
  };
};

export default usePasswordWithConfirmInput;
