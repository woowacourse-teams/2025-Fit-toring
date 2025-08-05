import { useState } from 'react';

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

  const {
    confirm: confirmUserId,
    matchConfirmed: userIdMatchConfirmed,
    shouldBlockSubmit: shouldBlockSubmitByUserId,
  } = useSubmitGuardWithConfirm(userId);

  const handleDuplicateConfirmClick = async () => {
    setDuplicateError(false);

    try {
      const response = await postValidateId(userId);

      if (response.status === 200) {
        confirmUserId();
        alert('사용 가능한 아이디입니다.');
      }
    } catch (error) {
      console.error('아이디 중복 확인 에러:', error);
      setDuplicateError(true);
    }
  };

  const getFinalUserIdErrorMessage = () => {
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
  };
};

export default useUserIdDuplicateCheck;
