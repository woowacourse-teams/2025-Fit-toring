import { useEffect, useRef, type RefObject } from 'react';

interface UsePullToRefreshParams {
  enabled: boolean;
  onRefresh: () => Promise<void> | void;
  rootRef: RefObject<HTMLElement | null>;
  indicatorRef: RefObject<HTMLElement | null>;
}

const REFRESH_THRESHOLD = 70;
const MAX_PULL_DISTANCE = 100;
const START_OFFSET = -48;

const getPullDistance = (distance: number) => {
  return Math.min(distance * 0.5, MAX_PULL_DISTANCE);
};

const setIndicatorStyle = (
  indicator: HTMLElement,
  pullDistance: number,
  opacity: number,
) => {
  indicator.style.opacity = `${opacity}`;
  indicator.style.transform = `translate3d(-50%, ${
    START_OFFSET + pullDistance
  }px, 0) rotate(0deg)`;
};

const resetIndicatorStyle = (indicator: HTMLElement) => {
  setIndicatorStyle(indicator, 0, 0);
};

const isAtTop = () => window.scrollY <= 0;

const usePullToRefresh = ({
  enabled,
  onRefresh,
  rootRef,
  indicatorRef,
}: UsePullToRefreshParams) => {
  const onRefreshRef = useRef(onRefresh);
  const startXRef = useRef(0);
  const startYRef = useRef(0);
  const isDraggingRef = useRef(false);
  const isRefreshingRef = useRef(false);
  const isPullGestureRef = useRef(false);
  const pullDistanceRef = useRef(0);

  useEffect(() => {
    onRefreshRef.current = onRefresh;
  }, [onRefresh]);

  useEffect(() => {
    const root = rootRef.current;

    if (!root || !enabled) {
      return;
    }

    const handleTouchStart = (event: TouchEvent) => {
      if (isRefreshingRef.current || event.touches.length !== 1 || !isAtTop()) {
        return;
      }

      const touch = event.touches[0];
      startXRef.current = touch.clientX;
      startYRef.current = touch.clientY;
      isDraggingRef.current = true;
      isPullGestureRef.current = false;
      pullDistanceRef.current = 0;
    };

    const handleTouchMove = (event: TouchEvent) => {
      if (!isDraggingRef.current || event.touches.length !== 1) {
        return;
      }

      const touch = event.touches[0];
      const diffX = touch.clientX - startXRef.current;
      const diffY = touch.clientY - startYRef.current;
      const isVerticalPull = diffY > 0 && Math.abs(diffY) > Math.abs(diffX);

      if (!isVerticalPull || !isAtTop()) {
        isDraggingRef.current = false;
        isPullGestureRef.current = false;
        pullDistanceRef.current = 0;
        return;
      }

      isPullGestureRef.current = true;
      event.preventDefault();

      const pullDistance = getPullDistance(diffY);
      const indicator = indicatorRef.current;
      pullDistanceRef.current = pullDistance;

      if (indicator) {
        setIndicatorStyle(
          indicator,
          pullDistance,
          Math.min(pullDistance / REFRESH_THRESHOLD, 1),
        );
      }
    };

    const handleTouchEnd = async () => {
      if (!isDraggingRef.current || !isPullGestureRef.current) {
        isDraggingRef.current = false;
        return;
      }

      isDraggingRef.current = false;
      isPullGestureRef.current = false;

      const indicator = indicatorRef.current;

      if (pullDistanceRef.current < REFRESH_THRESHOLD) {
        pullDistanceRef.current = 0;

        if (indicator) {
          resetIndicatorStyle(indicator);
        }

        return;
      }

      isRefreshingRef.current = true;
      pullDistanceRef.current = 0;

      try {
        await onRefreshRef.current();
      } finally {
        isRefreshingRef.current = false;

        if (indicator) {
          resetIndicatorStyle(indicator);
        }
      }
    };

    const handleTouchCancel = () => {
      isDraggingRef.current = false;
      isPullGestureRef.current = false;
      pullDistanceRef.current = 0;

      const indicator = indicatorRef.current;

      if (indicator) {
        resetIndicatorStyle(indicator);
      }
    };

    root.addEventListener('touchstart', handleTouchStart);
    root.addEventListener('touchmove', handleTouchMove, { passive: false });
    root.addEventListener('touchend', handleTouchEnd);
    root.addEventListener('touchcancel', handleTouchCancel);

    return () => {
      root.removeEventListener('touchstart', handleTouchStart);
      root.removeEventListener('touchmove', handleTouchMove);
      root.removeEventListener('touchend', handleTouchEnd);
      root.removeEventListener('touchcancel', handleTouchCancel);
    };
  }, [enabled, indicatorRef, rootRef]);
};

export default usePullToRefresh;
