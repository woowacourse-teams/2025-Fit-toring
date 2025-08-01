import { useState } from 'react';

import { ERROR_MESSAGE } from '../constants/errorMessage';

const VERIFICATION_CODE_LENGTH = 6;

const useVerificationCodeInput = () => {
  const [verificationCode, setVerificationCode] = useState('');

  const handleVerificationCodeChange = (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const onlyNumberRegex = /^[0-9]*$/; // 숫자만 가능

    if (onlyNumberRegex.test(e.target.value)) {
      setVerificationCode(e.target.value);
    }
  };

  const getVerificationCodeErrorMessage = () => {
    if (verificationCode.length === 0) {
      return '';
    }

    if (verificationCode.length < VERIFICATION_CODE_LENGTH) {
      return ERROR_MESSAGE.INVALID_PASSWORD_LENGTH;
    }

    return '';
  };

  return {
    verificationCode,
    handleVerificationCodeChange,
    errorMessage: getVerificationCodeErrorMessage(),
  };
};

export default useVerificationCodeInput;
