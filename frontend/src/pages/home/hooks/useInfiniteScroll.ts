import { useEffect, useRef } from 'react';

const useInfiniteScroll = <T extends HTMLElement>(
  callback: () => Promise<void>,
  isReady: boolean,
) => {
  const elementRef = useRef<T>(null);
  const callbackRef = useRef(callback);

  useEffect(() => {
    callbackRef.current = callback;
  }, [callback]);

  useEffect(() => {
    const observerFunction = async (entries: IntersectionObserverEntry[]) => {
      if (entries[0].isIntersecting && isReady) {
        await callbackRef.current();
      }
    };

    const io = new IntersectionObserver(observerFunction);
    if (elementRef.current) {
      io.observe(elementRef.current);
    }
    return () => io.disconnect();
  }, [isReady]);

  return {
    elementRef,
  };
};

export default useInfiniteScroll;
