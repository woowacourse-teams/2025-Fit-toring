import { useEffect, useLayoutEffect, useRef } from 'react';

interface useUpwardInfiniteScrollParams {
  onIntersect: () => void | Promise<void>;
  anchorKey: number | string;
  listRef: React.RefObject<HTMLDivElement | null>;
  shouldTrigger: () => boolean;
}

const useUpwardInfiniteScroll = ({
  onIntersect,
  anchorKey,
  listRef,
  shouldTrigger,
}: useUpwardInfiniteScrollParams) => {
  const pageFirstRef = useRef<HTMLDivElement | null>(null);

  const expectPrependRef = useRef<null | { prevH: number; prevTop: number }>(
    null,
  );

  useEffect(() => {
    const target = pageFirstRef.current;
    const list = listRef.current;

    if (!target || !list) {
      return;
    }

    const observer = new IntersectionObserver(
      async (entries) => {
        if (!entries[0].isIntersecting || !shouldTrigger()) {
          return;
        }

        expectPrependRef.current = {
          prevH: list.scrollHeight,
          prevTop: list.scrollTop,
        };

        await onIntersect();
      },
      {
        root: listRef.current,
        threshold: 0.1,
        rootMargin: '20px 0px 0px 0px',
      },
    );

    observer.observe(target);

    return () => observer.disconnect();
  }, [onIntersect, shouldTrigger, pageFirstRef.current, listRef.current]);

  useLayoutEffect(() => {
    const list = listRef.current;
    const snap = expectPrependRef.current;
    if (!list || !snap) {
      return;
    }

    const delta = list.scrollHeight - snap.prevH;
    list.scrollTop = snap.prevTop + delta;

    expectPrependRef.current = null;
  }, [listRef, anchorKey]);

  return { pageFirstRef };
};

export default useUpwardInfiniteScroll;
