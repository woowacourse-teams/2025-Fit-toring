import { useEffect, useState } from 'react';

import { introduceValidator } from '../utils/introduceValidator';

export const useIntroduceError = (introduction: string | null) => {
  const [introduceErrorMessage, setIntroduceErrorMessage] =
    useState<string>('');

  useEffect(() => {
    setIntroduceErrorMessage(introduceValidator(introduction));
  }, [introduction]);

  return { introduceErrorMessage };
};
