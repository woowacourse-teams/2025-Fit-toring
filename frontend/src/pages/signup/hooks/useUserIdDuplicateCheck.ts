import { useState } from 'react';

import { useMutation } from '@tanstack/react-query';

import { captureSentryError } from '../../../common/utils/captureSentryError';
import { postValidateId } from '../apis/postValidateId';

interface useUserIdDuplicateCheckParams {
  userId: string;
  userIdErrorMessage: string;
}

const useUserIdDuplicateCheck = ({ userId }: useUserIdDuplicateCheckParams) => {
  const [duplicateError, setDuplicateError] = useState(false);
  const [duplicateChecked, setDuplicateChecked] = useState(false);

  const { mutate: validateIdMutate } = useMutation({
    mutationFn: postValidateId,
    onSuccess: (response) => {
      if (response.status === 200) {
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

  return {
    duplicateError,
    handleDuplicateConfirmClick,
    resetDuplicateCheck,
    duplicateChecked,
  };
};

export default useUserIdDuplicateCheck;
