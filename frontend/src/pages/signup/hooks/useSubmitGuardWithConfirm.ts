import { useState } from 'react';

const useSubmitGuardWithConfirm = <T>(value: T) => {
  const [lastConfirmedValue, setLastConfirmedValue] = useState<T | null>(null);
  const [isCheckNeeded, setIsCheckNeeded] = useState(true);

  const confirm = () => {
    setLastConfirmedValue(value);
    setIsCheckNeeded(true);
  };

  const shouldBlockSubmit = () => {
    if (!isMatchedValue) {
      setIsCheckNeeded(false);
      return true;
    }
    return false;
  };

  const isMatchedValue = value === lastConfirmedValue;

  return {
    confirm,
    isCheckNeeded,
    shouldBlockSubmit,
  };
};

export default useSubmitGuardWithConfirm;
