import { useCallback, useEffect, useRef, useState } from 'react';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const useAsyncLock = <T extends (...args: any[]) => Promise<any>>({
  callback,
}: {
  callback: T;
}) => {
  const [isLoading, setIsLoading] = useState(false);
  const lockedRef = useRef(false);
  const callbackRef = useRef(callback);

  useEffect(() => {
    callbackRef.current = callback;
  }, [callback]);

  const lockedCallback = useCallback(
    async (
      ...args: Parameters<T>
    ): Promise<Awaited<ReturnType<T>> | undefined> => {
      if (lockedRef.current) {
        return;
      }
      setIsLoading(true);
      lockedRef.current = true;

      try {
        return await callbackRef.current(...args);
      } catch (error) {
        console.error('Error in lockedCallback:', error);
        throw error;
      } finally {
        setIsLoading(false);
        lockedRef.current = false;
      }
    },
    [],
  );

  return { isLoading, lockedCallback };
};

export default useAsyncLock;
