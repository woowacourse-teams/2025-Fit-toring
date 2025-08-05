import { useState } from 'react';

const useSubmitGuardWithConfirm = <T>(value: T) => {
  const [lastConfirmedValue, setLastConfirmedValue] = useState<T | null>(null);
  const [matchConfirmed, setMatchConfirmed] = useState(true);

  const confirm = () => {
    setLastConfirmedValue(value);
    setMatchConfirmed(true);
  };

  const shouldBlockSubmit = () => {
    if (!isMatchedValue) {
      setMatchConfirmed(false);
      return true;
    }
    return false;
  };

  const isMatchedValue = value === lastConfirmedValue;

  return {
    confirm,
    matchConfirmed,
    shouldBlockSubmit,
  };
};

export default useSubmitGuardWithConfirm;
