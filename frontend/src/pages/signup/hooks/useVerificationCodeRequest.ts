import { postAuthCode } from '../apis/postAuthCode';

import useSubmitGuardWithConfirm from './useSubmitGuardWithConfirm';
import { captureSentryError } from '../../../common/utils/captureSentryError';

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

  const handleAuthCodeClick = async (phoneNumber: string) => {
    try {
      const response = await postAuthCode(phoneNumber);
      if (response.status === 201) {
        alert('인증요청 성공');
        confirmPhoneNumber();
        completeRequest();
      }
    } catch (error) {
      console.error('인증요청 실패', error);

      captureSentryError({
        error,
        level: 'error',
        feature: 'sms',
        step: 'send-code',
      });
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
  };
};

export default useVerificationCodeRequest;
