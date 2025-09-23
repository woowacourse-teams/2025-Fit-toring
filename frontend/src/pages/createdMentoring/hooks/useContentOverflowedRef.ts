import { useCallback, useLayoutEffect, useRef, useState } from 'react';

const useContentOverflowedRef = () => {
  const [contentOverflowed, setContentOverflowed] = useState(false);

  const ref = useRef<HTMLElement>(null);

  useLayoutEffect(() => {
    if (!ref.current) {
      return;
    }

    setContentOverflowed(ref.current.scrollHeight > ref.current.clientHeight);
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

  return { contentOverflowed, setRef };
};

export default useContentOverflowedRef;
