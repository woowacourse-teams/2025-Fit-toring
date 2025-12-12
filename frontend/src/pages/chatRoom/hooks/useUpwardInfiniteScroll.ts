import { useEffect, useLayoutEffect, useRef } from 'react';

import { captureSentryError } from '../../../common/utils/captureSentryError';

interface useUpwardInfiniteScrollParams {
  onIntersect: () => void | Promise<void>;
  anchorKey: number | string;
  listElRef: React.RefObject<HTMLDivElement | null>;
  shouldTrigger: () => boolean;
}

const useUpwardInfiniteScroll = ({
  onIntersect,
  anchorKey,
  listElRef,
  shouldTrigger,
}: useUpwardInfiniteScrollParams) => {
  const pageFirstElRef = useRef<HTMLDivElement | null>(null);

  const expectPrependRef = useRef<null | { prevH: number; prevTop: number }>(
    null,
  );

  useEffect(() => {
    const target = pageFirstElRef.current;
    const list = listElRef.current;

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

        try {
          await onIntersect();
        } catch (error) {
          console.error(error);

          captureSentryError({
            error,
            level: 'warning',
            feature: 'chat',
            step: 'chat-history',
          });
        }
      },
      {
        root: list,
        threshold: 0.1,
        rootMargin: '20px 0px 0px 0px',
      },
    );

    observer.observe(target);

    return () => observer.disconnect();
  }, [onIntersect, shouldTrigger, listElRef]);

  useLayoutEffect(() => {
    const list = listElRef.current;
    const snap = expectPrependRef.current;
    if (!list || !snap) {
      return;
    }

    const delta = list.scrollHeight - snap.prevH;
    list.scrollTop = snap.prevTop + delta;

    expectPrependRef.current = null;
  }, [listElRef, anchorKey]);

  return { pageFirstElRef };
};

export default useUpwardInfiniteScroll;
