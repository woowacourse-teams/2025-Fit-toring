import { useState } from 'react';

import { useMutation } from '@tanstack/react-query';

import { postAuthCodeVerify } from '../apis/postAuthCodeVerify';
import { captureSentryError } from '../utils/captureSentryError';

import useSubmitGuardWithConfirm from './useSubmitGuardWithConfirm';

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

  const { mutate: verifyAuthCode } = useMutation({
    mutationFn: (phoneNumber: string) =>
      postAuthCodeVerify(phoneNumber, verificationCode),
    onMutate: () => {
      setVerificationCodeError(false);
    },
    onSuccess: (response) => {
      if (response.status === 200) {
        alert('인증 성공');
      }
    },
    onError: (error) => {
      setVerificationCodeError(true);
      console.error('인증 실패', error);
      captureSentryError({
        error,
        level: 'error',
        feature: 'sms',
        step: 'verify-code',
      });
    },
    onSettled: () => {
      completeVerification();
      confrimVerificationCode();
    },
  });

  const handleAuthCodeVerifyClick = (phoneNumber: string) => {
    verifyAuthCode(phoneNumber);
  };

  const getFinalVerificationCodeErrorMessage = () => {
    if (verificationCodeErrorMessage) {
      return verificationCodeErrorMessage;
    }

    if (verificationCodeMatchConfirmed && verificationCodeError) {
      return '인증 실패';
    }

    if (!verificationCodeMatchConfirmed) {
      return '인증을 해주세요';
    }

    return '';
  };

  return {
    verificationCodeError,
    handleAuthCodeVerifyClick,
    getFinalVerificationCodeErrorMessage,
    shouldBlockSubmitByVerificationCode,
  };
};

export default useVerificationCodeConfirm;
