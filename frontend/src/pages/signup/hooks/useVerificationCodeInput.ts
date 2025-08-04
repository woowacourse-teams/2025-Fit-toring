import { useState } from 'react';

import { postAuthCodeVerify } from '../apis/postAuthCodeVerify';
import { ERROR_MESSAGE } from '../constants/errorMessage';

export const VERIFICATION_CODE_LENGTH = 6;

const useVerificationCodeInput = () => {
  const [verificationCode, setVerificationCode] = useState('');
  const [verificationCodeError, setVerificationCodeError] = useState(false);

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

    if (verificationCodeError) {
      return '인증 실패';
    }

    if (verificationCode.length < VERIFICATION_CODE_LENGTH) {
      return ERROR_MESSAGE.INVALID_PASSWORD_LENGTH;
    }

    return '';
  };

  const handleAuthCodeVerifyClick = async (phoneNumber: string) => {
    setVerificationCodeError(false);
    try {
      const response = await postAuthCodeVerify(phoneNumber, verificationCode);
      if (response.status === 200) {
        alert('인증 성공');
      }
    } catch (error) {
      setVerificationCodeError(true);
      console.error('인증 실패', error);
    }
  };

  const errorMessage = getVerificationCodeErrorMessage();

  return {
    verificationCode,
    handleVerificationCodeChange,
    handleAuthCodeVerifyClick,
    errorMessage,
    isValid: verificationCode !== '' && errorMessage === '',
  };
};

export default useVerificationCodeInput;
