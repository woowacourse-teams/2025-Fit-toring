import { useCallback, useLayoutEffect, useRef } from 'react';

interface PrevScroll {
  scrollTop: number;
  scrollHeight: number;
  clientHeight: number;
}

type MessageId = number | null | undefined;

interface useScrollToBottomOnMessageSendParams {
  firstId: MessageId;
  lastId: MessageId;
  listElRef: React.RefObject<HTMLDivElement | null>;
}

const BOTTOM_THRESHOLD_PX = 50;

const useScrollToBottomOnMessageSend = ({
  firstId,
  lastId,
  listElRef,
}: useScrollToBottomOnMessageSendParams) => {
  const prevScrollRef = useRef<PrevScroll>({
    scrollTop: 0,
    scrollHeight: 0,
    clientHeight: 0,
  });

  const prevIdsRef = useRef<{ firstId: MessageId; lastId: MessageId }>({
    firstId: null,
    lastId: null,
  });

  const capturePrevScroll = useCallback(() => {
    const element = listElRef.current;
    if (!element) {
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
    if (!element) {
      return;
    }

    const prevScroll = prevScrollRef.current;
    const prevIds = prevIdsRef.current;

    if (prevIds.firstId === null || prevIds.lastId === null) {
      prevIdsRef.current = { firstId, lastId };
      return;
    }

    if (firstId === null || lastId === null) {
      prevIdsRef.current = { firstId, lastId };
      return;
    }

    const firstChanged = firstId !== prevIds.firstId;
    const lastChanged = lastId !== prevIds.lastId;

    if (firstChanged) {
      prevIdsRef.current = { firstId, lastId };
      return;
    }

    if (lastChanged && !firstChanged) {
      const wasAtBottom =
        prevScroll.scrollHeight -
          prevScroll.scrollTop -
          prevScroll.clientHeight <
        BOTTOM_THRESHOLD_PX;

      if (wasAtBottom) {
        element.scrollTop = element.scrollHeight;
      }
    }

    prevIdsRef.current = { firstId, lastId };
  }, [firstId, lastId, listElRef]);

  return { capturePrevScroll };
};

export default useScrollToBottomOnMessageSend;
