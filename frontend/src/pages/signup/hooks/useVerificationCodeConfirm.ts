import { useState } from 'react';

import { postAuthCodeVerify } from '../apis/postAuthCodeVerify';

import { useConfirmState } from './useConfirmStatus';

interface useVerificationCodeConfirmParams {
  verificationCode: string;
  verificationCodeErrorMessage: string;
}

const useVerificationCodeConfirm = ({
  verificationCode,
  verificationCodeErrorMessage,
}: useVerificationCodeConfirmParams) => {
  const {
    confirm: verificationCodeConfirm,
    isCheckNeeded: isVerificationCodeCheck,
    shouldBlockSubmit: shouldBlockSubmitByVerificationCode,
  } = useConfirmState(verificationCode);

  const [verificationCodeError, setVerificationCodeError] = useState(false);

  const handleAuthCodeVerifyClick = async (phoneNumber: string) => {
    setVerificationCodeError(false);
    try {
      const response = await postAuthCodeVerify(phoneNumber, verificationCode);
      if (response.status === 201) {
        alert('인증 성공');
        verificationCodeConfirm();
      }
    } catch (error) {
      setVerificationCodeError(true);
      console.error('인증 실패', error);
    }
  };

  const getFinalVerificationCodeErrorMessage = () => {
    if (!isVerificationCodeCheck) {
      return '인증을 해주세요';
    }
    if (verificationCodeError) {
      return '인증 실패';
    }

    return verificationCodeErrorMessage;
  };

  return {
    verificationCodeError,
    handleAuthCodeVerifyClick,
    getFinalVerificationCodeErrorMessage,
    shouldBlockSubmitByVerificationCode,
  };
};

export default useVerificationCodeConfirm;
