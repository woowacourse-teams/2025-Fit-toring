import { useCallback, useLayoutEffect, useRef } from 'react';

interface PrevScroll {
  scrollTop: number;
  scrollHeight: number;
  clientHeight: number;
}

interface useScrollToBottomOnMessageSendParams {
  messageCount: number;
  listElRef: React.RefObject<HTMLDivElement | null>;
}

const useScrollToBottomOnMessageSend = ({
  messageCount,
  listElRef,
}: useScrollToBottomOnMessageSendParams) => {
  const prevScrollRef = useRef<PrevScroll>({
    scrollTop: 0,
    scrollHeight: 0,
    clientHeight: 0,
  });

  const capturePrevScroll = useCallback(() => {
    const element = listElRef.current;
    const prev = prevScrollRef.current;

    if (!element || !prev) {
      return;
    }

    prevScrollRef.current = {
      scrollTop: element.scrollTop,
      scrollHeight: element.scrollHeight,
      clientHeight: element.clientHeight,
    };
  }, [listElRef]);

  useLayoutEffect(() => {
    const element = listElRef.current;
    const prev = prevScrollRef.current;

    if (!element || !prev) {
      return;
    }

    const isAtBottom =
      prev.scrollHeight - prev.scrollTop - prev.clientHeight < 50;

    if (isAtBottom) {
      element.scrollTop = element.scrollHeight;
    }
  }, [listElRef, messageCount, prevScrollRef]);

  return {
    prevScrollRef,
    capturePrevScroll,
  };
};

export default useScrollToBottomOnMessageSend;
