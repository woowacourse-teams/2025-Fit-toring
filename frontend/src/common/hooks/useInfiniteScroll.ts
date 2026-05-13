import { useEffect, useRef } from 'react';

interface UseInfiniteScrollParams {
  isReady: boolean;
  onIntersect: () => void | Promise<void>;
}

const useInfiniteScroll = <T extends HTMLElement>({
  isReady,
  onIntersect,
}: UseInfiniteScrollParams) => {
  const targetRef = useRef<T | null>(null);
  const onIntersectRef = useRef(onIntersect);

  useEffect(() => {
    onIntersectRef.current = onIntersect;
  }, [onIntersect]);

  useEffect(() => {
    const target = targetRef.current;

    if (!target) {
      return;
    }

    const observer = new IntersectionObserver(async (entries) => {
      if (!entries[0].isIntersecting || !isReady) {
        return;
      }

      await onIntersectRef.current();
    });

    observer.observe(target);

    return () => observer.disconnect();
  }, [isReady]);

  return { targetRef };
};

export default useInfiniteScroll;
