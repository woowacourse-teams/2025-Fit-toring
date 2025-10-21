import {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from 'react';

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

  const [ready, setReady] = useState(false);

  const listReadyRef = useCallback(
    (node: HTMLDivElement | null) => {
      listElRef.current = node;
      setReady(!!node && !!pageFirstElRef.current);
    },
    [listElRef],
  );

  const pageFirstReadyRef = useCallback(
    (node: HTMLDivElement | null) => {
      pageFirstElRef.current = node;
      setReady(!!node && !!listElRef.current);
    },
    [listElRef],
  );

  useEffect(() => {
    const target = pageFirstElRef.current;
    const list = listElRef.current;

    if (!ready || !target || !list) {
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
        root: listElRef.current,
        threshold: 0.1,
        rootMargin: '20px 0px 0px 0px',
      },
    );

    observer.observe(target);

    return () => observer.disconnect();
  }, [onIntersect, shouldTrigger, listElRef, ready]);

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

  return { listReadyRef, pageFirstReadyRef };
};

export default useUpwardInfiniteScroll;
