import { useEffect, useRef, type RefObject } from 'react';

interface UsePullToRefreshParams {
  enabled: boolean;
  onRefresh: () => Promise<void> | void;
  rootRef: RefObject<HTMLElement | null>;
  indicatorRef: RefObject<HTMLElement | null>;
  contentRef: RefObject<HTMLElement | null>;
}

const REFRESH_THRESHOLD = 70;
const MAX_PULL_DISTANCE = 100;
const MAX_PULL_ROTATION_DEGREE = 180;
const RESET_TRANSITION = 'transform 180ms ease-out, opacity 180ms ease-out';
const ROTATION_CUSTOM_PROPERTY = '--pull-to-refresh-rotation';
const SCROLL_TOP_THRESHOLD = 1;

export const PULL_TO_REFRESH_REFRESHING_CLASS =
  'pull-to-refresh-indicator--refreshing';

const getPullDistance = (distance: number) => {
  return Math.min(distance * 0.5, MAX_PULL_DISTANCE);
};

const setIndicatorStyle = (
  indicator: HTMLElement,
  pullDistance: number,
  opacity: number,
) => {
  const rotationDegree =
    Math.min(pullDistance / REFRESH_THRESHOLD, 1) * MAX_PULL_ROTATION_DEGREE;

  indicator.style.opacity = `${opacity}`;
  indicator.style.setProperty(
    ROTATION_CUSTOM_PROPERTY,
    `${rotationDegree}deg`,
  );
};

const resetIndicatorStyle = (indicator: HTMLElement) => {
  setIndicatorStyle(indicator, 0, 0);
};

const setContentStyle = (content: HTMLElement, pullDistance: number) => {
  content.style.transform = `translate3d(0, ${pullDistance}px, 0)`;
};

const resetContentStyle = (content: HTMLElement) => {
  setContentStyle(content, 0);
};

const getElementFromTarget = (target: EventTarget | null) => {
  if (target instanceof HTMLElement) {
    return target;
  }

  if (target instanceof Node) {
    return target.parentElement;
  }

  return null;
};

const isScrollableElement = (element: HTMLElement) => {
  const { overflowY } = window.getComputedStyle(element);
  const canScrollY =
    overflowY === 'auto' || overflowY === 'scroll' || overflowY === 'overlay';

  return canScrollY && element.scrollHeight > element.clientHeight;
};

const isElementScrollAtTop = (element: HTMLElement) => {
  return element.scrollTop <= SCROLL_TOP_THRESHOLD;
};

const isDocumentScrollAtTop = () => {
  return (
    window.scrollY <= SCROLL_TOP_THRESHOLD &&
    document.documentElement.scrollTop <= SCROLL_TOP_THRESHOLD &&
    document.body.scrollTop <= SCROLL_TOP_THRESHOLD
  );
};

const areScrollableAncestorsAtTop = (
  target: EventTarget | null,
  fallbackRoot: HTMLElement,
) => {
  let element = getElementFromTarget(target) ?? fallbackRoot;

  while (element) {
    if (isScrollableElement(element) && !isElementScrollAtTop(element)) {
      return false;
    }

    element = element.parentElement;
  }

  return true;
};

const canStartPull = (target: EventTarget | null, root: HTMLElement) => {
  return isDocumentScrollAtTop() && areScrollableAncestorsAtTop(target, root);
};

const usePullToRefresh = ({
  enabled,
  onRefresh,
  rootRef,
  indicatorRef,
  contentRef,
}: UsePullToRefreshParams) => {
  const onRefreshRef = useRef(onRefresh);
  const startXRef = useRef(0);
  const startYRef = useRef(0);
  const isDraggingRef = useRef(false);
  const isRefreshingRef = useRef(false);
  const pullDistanceRef = useRef(0);
  const frameIdRef = useRef<number | null>(null);
  const nextPullDistanceRef = useRef(0);
  const nextOpacityRef = useRef(0);

  useEffect(() => {
    onRefreshRef.current = onRefresh;
  }, [onRefresh]);

  useEffect(() => {
    const root = rootRef.current;

    if (!root || !enabled) {
      return;
    }

    const cancelPendingFrame = () => {
      if (frameIdRef.current === null) {
        return;
      }

      window.cancelAnimationFrame(frameIdRef.current);
      frameIdRef.current = null;
    };

    const clearPullTransition = () => {
      const indicator = indicatorRef.current;
      const content = contentRef.current;

      if (indicator) {
        indicator.style.transition = '';
        indicator.style.willChange = 'opacity, transform';
      }

      if (content) {
        content.style.transition = '';
        content.style.willChange = 'transform';
      }
    };

    const setPullTransition = () => {
      const indicator = indicatorRef.current;
      const content = contentRef.current;

      if (indicator) {
        indicator.style.transition = RESET_TRANSITION;
      }

      if (content) {
        content.style.transition = RESET_TRANSITION;
      }
    };

    const clearWillChange = () => {
      const indicator = indicatorRef.current;
      const content = contentRef.current;

      if (indicator) {
        indicator.style.willChange = '';
      }

      if (content) {
        content.style.willChange = '';
      }
    };

    const schedulePullStyle = (pullDistance: number, opacity: number) => {
      nextPullDistanceRef.current = pullDistance;
      nextOpacityRef.current = opacity;

      if (frameIdRef.current !== null) {
        return;
      }

      frameIdRef.current = window.requestAnimationFrame(() => {
        frameIdRef.current = null;

        const indicator = indicatorRef.current;
        const content = contentRef.current;

        if (indicator) {
          setIndicatorStyle(
            indicator,
            nextPullDistanceRef.current,
            nextOpacityRef.current,
          );
        }

        if (content) {
          setContentStyle(content, nextPullDistanceRef.current);
        }
      });
    };

    const resetPullStyle = () => {
      cancelPendingFrame();
      setPullTransition();

      const indicator = indicatorRef.current;
      const content = contentRef.current;

      if (indicator) {
        indicator.classList.remove(PULL_TO_REFRESH_REFRESHING_CLASS);
        resetIndicatorStyle(indicator);
      }

      if (content) {
        resetContentStyle(content);
      }

      clearWillChange();
    };

    const holdPullStyleForRefresh = () => {
      cancelPendingFrame();
      setPullTransition();

      const indicator = indicatorRef.current;
      const content = contentRef.current;

      if (indicator) {
        setIndicatorStyle(indicator, REFRESH_THRESHOLD, 1);
        indicator.classList.add(PULL_TO_REFRESH_REFRESHING_CLASS);
      }

      if (content) {
        setContentStyle(content, REFRESH_THRESHOLD);
      }
    };

    const handleTouchStart = (event: TouchEvent) => {
      if (
        isRefreshingRef.current ||
        event.touches.length !== 1 ||
        !canStartPull(event.target, root)
      ) {
        return;
      }

      const touch = event.touches[0];
      startXRef.current = touch.clientX;
      startYRef.current = touch.clientY;
      isDraggingRef.current = true;
      pullDistanceRef.current = 0;
      clearPullTransition();
    };

    const handleTouchMove = (event: TouchEvent) => {
      if (!isDraggingRef.current || event.touches.length !== 1) {
        return;
      }

      const touch = event.touches[0];
      const diffX = touch.clientX - startXRef.current;
      const diffY = touch.clientY - startYRef.current;
      const isVerticalPull = diffY > 0 && Math.abs(diffY) > Math.abs(diffX);

      if (!isVerticalPull || !canStartPull(event.target, root)) {
        isDraggingRef.current = false;
        pullDistanceRef.current = 0;
        resetPullStyle();
        return;
      }

      event.preventDefault();

      const pullDistance = getPullDistance(diffY);
      pullDistanceRef.current = pullDistance;

      schedulePullStyle(
        pullDistance,
        Math.min(pullDistance / REFRESH_THRESHOLD, 1),
      );
    };

    const handleTouchEnd = async () => {
      if (!isDraggingRef.current || pullDistanceRef.current === 0) {
        isDraggingRef.current = false;
        return;
      }

      isDraggingRef.current = false;

      if (pullDistanceRef.current < REFRESH_THRESHOLD) {
        pullDistanceRef.current = 0;
        resetPullStyle();
        return;
      }

      isRefreshingRef.current = true;
      pullDistanceRef.current = 0;
      holdPullStyleForRefresh();

      try {
        await onRefreshRef.current();
      } finally {
        isRefreshingRef.current = false;
        resetPullStyle();
      }
    };

    const handleTouchCancel = () => {
      isDraggingRef.current = false;
      pullDistanceRef.current = 0;

      resetPullStyle();
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
      cancelPendingFrame();
    };
  }, [contentRef, enabled, indicatorRef, rootRef]);
};

export default usePullToRefresh;
