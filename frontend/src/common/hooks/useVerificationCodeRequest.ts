import { useMutation } from '@tanstack/react-query';

import { postAuthCode } from '../apis/postAuthCode';
import { captureSentryError } from '../utils/captureSentryError';

import useSubmitGuardWithConfirm from './useSubmitGuardWithConfirm';

interface useVerificationCodeRequestParams {
  phoneNumber: string;
  phoneNumberErrorMessage: string;
  completeRequest: () => void;
}

const useVerificationCodeRequest = ({
  phoneNumber,
  phoneNumberErrorMessage,
  completeRequest,
}: useVerificationCodeRequestParams) => {
  const {
    confirm: confirmPhoneNumber,
    matchConfirmed: matchConfirmedPhoneNumber,
    shouldBlockSubmit: shouldBlockSubmitByPhoneNumberCheck,
  } = useSubmitGuardWithConfirm(phoneNumber);

  const { mutate: requestAuthCode } = useMutation({
    mutationFn: (phoneNumber: string) => postAuthCode(phoneNumber),
    onSuccess: (response) => {
      if (response.status === 201) {
        alert('인증요청 성공');
        confirmPhoneNumber();
        completeRequest();
      }
    },
    onError: (error) => {
      console.error('인증요청 실패', error);
      captureSentryError({
        error,
        level: 'error',
        feature: 'sms',
        step: 'send-code',
      });
    },
  });

  const handleAuthCodeClick = (phoneNumber: string) => {
    requestAuthCode(phoneNumber);
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
  };
};

export default useVerificationCodeRequest;
