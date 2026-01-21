import { useCallback, useEffect, useRef, useState } from 'react';

type AsyncFn<TArgs extends unknown[], TResult> = (
  ...args: TArgs
) => Promise<TResult>;

const useAsyncLock = <TArgs extends unknown[], TResult>({
  callback,
}: {
  callback: AsyncFn<TArgs, TResult>;
}) => {
  const [isLoading, setIsLoading] = useState(false);
  const lockedRef = useRef(false);
  const callbackRef = useRef(callback);

  useEffect(() => {
    callbackRef.current = callback;
  }, [callback]);

  const lockedCallback = useCallback(
    async (...args: TArgs): Promise<Awaited<TResult> | undefined> => {
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
