import { useState } from 'react';

import { postAuthCode } from '../apis/postAuthCode';

import useSubmitGuardWithConfirm from './useSubmitGuardWithConfirm';

interface useVerificationCodeRequestParams {
  phoneNumber: string;
  phoneNumberErrorMessage: string;
}

const useVerificationCodeRequest = ({
  phoneNumber,
  phoneNumberErrorMessage,
}: useVerificationCodeRequestParams) => {
  const {
    confirm: confirmPhoneNumber,
    matchConfirmed: matchConfirmedPhoneNumber,
    shouldBlockSubmit: shouldBlockSubmitByPhoneNumberCheck,
  } = useSubmitGuardWithConfirm(phoneNumber);

  const [requestCompleted, setRequestCompleted] = useState(false);

  const handleAuthCodeClick = async (phoneNumber: string) => {
    try {
      const response = await postAuthCode(phoneNumber);
      if (response.status === 201) {
        alert('인증요청 성공');
        confirmPhoneNumber();
        setRequestCompleted(true);
      }
    } catch (error) {
      console.error('인증요청 실패', error);
    }
  };

  const getFinalPhoneNumberErrorMessage = () => {
    if (!matchConfirmedPhoneNumber) {
      return '인증요청을 해주세요.';
    }

    return phoneNumberErrorMessage;
  };

  return {
    shouldBlockSubmitByPhoneNumberCheck,
    handleAuthCodeClick,
    getFinalPhoneNumberErrorMessage,
    matchConfirmedPhoneNumber,
    requestCompleted,
  };
};

export default useVerificationCodeRequest;
