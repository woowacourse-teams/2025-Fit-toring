import { useState } from 'react';

import { captureSentryError } from '../../../common/utils/captureSentryError';
import { postValidateId } from '../apis/postValidateId';

interface useUserIdDuplicateCheckParams {
  userId: string;
  userIdErrorMessage: string;
}

const useUserIdDuplicateCheck = ({ userId }: useUserIdDuplicateCheckParams) => {
  const [duplicateError, setDuplicateError] = useState(false);
  const [duplicateChecked, setDuplicateChecked] = useState(false);

  const handleDuplicateConfirmClick = async () => {
    setDuplicateError(false);

    try {
      const response = await postValidateId(userId);

      if (response.status === 200) {
        setDuplicateChecked(true);
      }
    } catch (error) {
      console.error('아이디 중복 확인 에러:', error);
      setDuplicateError(true);

      captureSentryError({
        error,
        level: 'warning',
        feature: 'signup',
        step: 'userId-duplicate-validate',
      });
    }
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
