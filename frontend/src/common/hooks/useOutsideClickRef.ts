import { useCallback, useEffect, useRef } from 'react';

const useOutsideClickRef = <T extends HTMLElement = HTMLElement>(
  callback: () => void,
) => {
  const ref = useRef<HTMLElement | null>(null);
  const callbackRef = useRef(callback);

  const setRef = useCallback((node: T | null) => {
    if (node === null) {
      return;
    }

    ref.current = node;

    return () => {
      ref.current = null;
    };
  }, []);

  useEffect(() => {
    callbackRef.current = callback;
  }, [callback]);

  useEffect(() => {
    const handleClickOutside = ({ target }: MouseEvent) => {
      if (target === null) {
        return;
      }

      if (ref.current === null) {
        return;
      }

      if (ref.current.contains(target as Node)) {
        return;
      }

      callbackRef.current();
    };

    document.addEventListener('click', handleClickOutside);
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, []);

  return {
    ref: setRef,
  };
};

export default useOutsideClickRef;
