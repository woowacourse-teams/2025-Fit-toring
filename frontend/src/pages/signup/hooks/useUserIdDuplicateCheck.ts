import { useEffect, useState } from 'react';

import { useMutation } from '@tanstack/react-query';

import { captureSentryError } from '../../../common/utils/captureSentryError';
import { postValidateId } from '../apis/postValidateId';

interface useUserIdDuplicateCheckParams {
  userId: string;
  userIdErrorMessage: string;
}

const useUserIdDuplicateCheck = ({ userId }: useUserIdDuplicateCheckParams) => {
  const [duplicateError, setDuplicateError] = useState(false);
  const [checkedUserId, setCheckedUserId] = useState<string | null>(null);

  useEffect(() => {
    setDuplicateError(false);
  }, [userId]);

  const { mutate: validateIdMutate } = useMutation({
    mutationFn: postValidateId,
    onSuccess: (response) => {
      if (response.status === 200) {
        setCheckedUserId(userId);
      }
    },
    onError: (error) => {
      console.error('아이디 중복 확인 에러:', error);
      setDuplicateError(true);
      setCheckedUserId(null);

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

  const duplicateChecked = checkedUserId === userId;

  return {
    duplicateError,
    handleDuplicateConfirmClick,
    duplicateChecked,
  };
};

export default useUserIdDuplicateCheck;
