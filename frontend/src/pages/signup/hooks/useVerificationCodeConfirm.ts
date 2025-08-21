import { useState } from 'react';

import { postAuthCodeVerify } from '../apis/postAuthCodeVerify';

import useSubmitGuardWithConfirm from './useSubmitGuardWithConfirm';
import { captureSentryError } from '../../../common/utils/captureSentryError';

interface useVerificationCodeConfirmParams {
  verificationCode: string;
  verificationCodeErrorMessage: string;
  completeVerification: () => void;
}

const useVerificationCodeConfirm = ({
  verificationCode,
  verificationCodeErrorMessage,
  completeVerification,
}: useVerificationCodeConfirmParams) => {
  const {
    confirm: confrimVerificationCode,
    matchConfirmed: verificationCodeMatchConfirmed,
    shouldBlockSubmit: shouldBlockSubmitByVerificationCode,
  } = useSubmitGuardWithConfirm(verificationCode);

  const [verificationCodeError, setVerificationCodeError] = useState(false);

  const handleAuthCodeVerifyClick = async (phoneNumber: string) => {
    setVerificationCodeError(false);
    try {
      const response = await postAuthCodeVerify(phoneNumber, verificationCode);
      if (response.status === 200) {
        alert('인증 성공');
        confrimVerificationCode();
        completeVerification();
      }
    } catch (error) {
      setVerificationCodeError(true);
      console.error('인증 실패', error);

      captureSentryError({
        error,
        level: 'error',
        feature: 'sms',
        step: 'verify-code',
      });
    }
  };

  const getFinalVerificationCodeErrorMessage = () => {
    if (!verificationCodeMatchConfirmed) {
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
