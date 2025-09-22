import { useCallback, useLayoutEffect, useRef, useState } from 'react';

const useClampedRef = () => {
  const [clamped, setClamped] = useState(false);

  const ref = useRef<HTMLElement>(null);

  useLayoutEffect(() => {
    if (!ref.current) {
      return;
    }

    setClamped(ref.current.scrollHeight > ref.current.clientHeight);
  }, []);

  const setRef = useCallback((element: HTMLElement | null) => {
    if (!element) {
      return;
    }

    ref.current = element;

    return () => {
      ref.current = null;
    };
  }, []);

  return { clamped, setRef };
};

export default useClampedRef;
