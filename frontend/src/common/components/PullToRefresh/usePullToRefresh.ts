import { useEffect, useRef, type RefObject } from 'react';

interface UsePullToRefreshParams {
  enabled: boolean;
  onRefresh: () => Promise<void> | void;
  rootRef: RefObject<HTMLElement | null>;
  indicatorRef: RefObject<HTMLElement | null>;
  contentRef: RefObject<HTMLElement | null>;
}

type PullPhase = 'idle' | 'pulling' | 'settling' | 'refreshing';

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
  const phaseRef = useRef<PullPhase>('idle');
  const frameIdRef = useRef<number | null>(null);
  const targetPullDistanceRef = useRef(0);
  const currentPullDistanceRef = useRef(0);

  useEffect(() => {
    onRefreshRef.current = onRefresh;
  }, [onRefresh]);

  useEffect(() => {
    const root = rootRef.current;

    if (!root || !enabled) {
      return;
    }

    const cancelAnimationFrameLoop = () => {
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

    const renderPullStyle = (pullDistance: number) => {
      const indicator = indicatorRef.current;
      const content = contentRef.current;
      const opacity = Math.min(pullDistance / REFRESH_THRESHOLD, 1);

      if (indicator) {
        setIndicatorStyle(indicator, pullDistance, opacity);
      }

      if (content) {
        setContentStyle(content, pullDistance);
      }
    };

    const stopAnimationFrameLoop = () => {
      cancelAnimationFrameLoop();
      clearWillChange();
    };

    const runAnimationFrameLoop = () => {
      frameIdRef.current = null;
      currentPullDistanceRef.current = targetPullDistanceRef.current;
      renderPullStyle(currentPullDistanceRef.current);

      if (phaseRef.current === 'idle') {
        stopAnimationFrameLoop();
      }
    };

    const startAnimationFrameLoop = () => {
      if (frameIdRef.current !== null) {
        return;
      }

      frameIdRef.current = window.requestAnimationFrame(runAnimationFrameLoop);
    };

    const resetPullStyle = () => {
      targetPullDistanceRef.current = 0;
      currentPullDistanceRef.current = 0;
      phaseRef.current = 'idle';
      stopAnimationFrameLoop();

      const indicator = indicatorRef.current;
      const content = contentRef.current;

      if (indicator) {
        indicator.classList.remove(PULL_TO_REFRESH_REFRESHING_CLASS);
        resetIndicatorStyle(indicator);
      }

      if (content) {
        resetContentStyle(content);
      }
    };

    const holdPullStyleForRefresh = () => {
      targetPullDistanceRef.current = REFRESH_THRESHOLD;
      currentPullDistanceRef.current = REFRESH_THRESHOLD;
      phaseRef.current = 'refreshing';
      stopAnimationFrameLoop();

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
      phaseRef.current = 'pulling';
      targetPullDistanceRef.current = 0;
      currentPullDistanceRef.current = 0;
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
        resetPullStyle();
        return;
      }

      event.preventDefault();

      const pullDistance = getPullDistance(diffY);
      targetPullDistanceRef.current = pullDistance;
      startAnimationFrameLoop();
    };

    const handleTouchEnd = async () => {
      if (!isDraggingRef.current || targetPullDistanceRef.current === 0) {
        isDraggingRef.current = false;
        return;
      }

      isDraggingRef.current = false;

      if (targetPullDistanceRef.current < REFRESH_THRESHOLD) {
        phaseRef.current = 'settling';
        resetPullStyle();
        return;
      }

      isRefreshingRef.current = true;
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
      phaseRef.current = 'settling';

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
      cancelAnimationFrameLoop();
    };
  }, [contentRef, enabled, indicatorRef, rootRef]);
};

export default usePullToRefresh;
