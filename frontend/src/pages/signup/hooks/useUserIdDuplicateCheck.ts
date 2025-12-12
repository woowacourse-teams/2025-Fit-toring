import { useState } from 'react';

import { useMutation } from '@tanstack/react-query';

import { captureSentryError } from '../../../common/utils/captureSentryError';
import { postValidateId } from '../apis/postValidateId';

import useSubmitGuardWithConfirm from './useSubmitGuardWithConfirm';

interface useUserIdDuplicateCheckParams {
  userId: string;
  userIdErrorMessage: string;
}

const useUserIdDuplicateCheck = ({
  userId,
  userIdErrorMessage,
}: useUserIdDuplicateCheckParams) => {
  const [duplicateError, setDuplicateError] = useState(false);
  const [duplicateChecked, setDuplicateChecked] = useState(false);

  const {
    confirm: confirmUserId,
    matchConfirmed: userIdMatchConfirmed,
    shouldBlockSubmit: shouldBlockSubmitByUserId,
  } = useSubmitGuardWithConfirm(userId);

  const { mutate: validateIdMutate } = useMutation({
    mutationFn: postValidateId,
    onSuccess: (response) => {
      if (response.status === 200) {
        confirmUserId();
        setDuplicateChecked(true);
      }
    },
    onError: (error) => {
      console.error('아이디 중복 확인 에러:', error);
      setDuplicateError(true);

      captureSentryError({
        error,
        level: 'warning',
        feature: 'signup',
        step: 'userId-duplicate-validate',
      });
    },
  });

  const handleDuplicateConfirmClick = async () => {
    setDuplicateError(false);

    validateIdMutate(userId);
  };

  const resetDuplicateCheck = () => {
    setDuplicateChecked(false);
    setDuplicateError(false);
  };

  const getFinalUserIdErrorMessage = () => {
    if (userIdErrorMessage !== '') {
      return userIdErrorMessage;
    }

    if (duplicateError) {
      return '이미 사용중인 아이디입니다.';
    }

    if (!userIdMatchConfirmed) {
      return '중복확인을 해주세요';
    }

    return userIdErrorMessage;
  };

  return {
    duplicateError,
    handleDuplicateConfirmClick,
    shouldBlockSubmitByUserId,
    getFinalUserIdErrorMessage,
    resetDuplicateCheck,
    duplicateChecked,
  };
};

export default useUserIdDuplicateCheck;
