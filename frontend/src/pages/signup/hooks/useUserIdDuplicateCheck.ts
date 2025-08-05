import { useState } from 'react';

import { postValidateId } from '../apis/postValidateId';

import { useConfirmState } from './useConfirmStatus';

interface useDuplicateCheckParams {
  userId: string;
  userIdErrorMessage: string;
}

const useUserIdDuplicateCheck = ({
  userId,
  userIdErrorMessage,
}: useDuplicateCheckParams) => {
  const [duplicateError, setDuplicateError] = useState(false);

  const {
    confirm: userIdConfirm,
    isCheckNeeded: isUserIdCheck,
    shouldBlockSubmit: shouldBlockSubmitByUserId,
  } = useConfirmState(userId);

  const handleDuplicateConfrimClick = async () => {
    setDuplicateError(false);

    try {
      const response = await postValidateId(userId);

      if (response.status === 200) {
        userIdConfirm();
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

    if (!isUserIdCheck) {
      return '중복확인을 해주세요';
    }

    return userIdErrorMessage;
  };

  return {
    duplicateError,
    handleDuplicateConfrimClick,
    shouldBlockSubmitByUserId,
    getFinalUserIdErrorMessage,
  };
};

export default useUserIdDuplicateCheck;
