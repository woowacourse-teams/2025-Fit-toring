export type ScrollSource = number | EventTarget | null;

const SCROLLABLE_OVERFLOW_PATTERN = /(auto|scroll|overlay)/;

type ScrollContainer = HTMLElement | 'document';

const getValidScrollY = (scrollY: unknown): number | null => {
  if (typeof scrollY !== 'number') {
    return null;
  }

  if (!Number.isFinite(scrollY) || scrollY < 0) {
    return null;
  }

  return scrollY;
};

const getElementFromTarget = (target: EventTarget | null | undefined) => {
  if (target instanceof HTMLElement) {
    return target;
  }

  if (target instanceof SVGElement) {
    return target.parentElement;
  }

  return null;
};

const getDocumentScrollElement = (): HTMLElement | null => {
  if (document.scrollingElement instanceof HTMLElement) {
    return document.scrollingElement;
  }

  if (document.documentElement instanceof HTMLElement) {
    return document.documentElement;
  }

  if (document.body instanceof HTMLElement) {
    return document.body;
  }

  return null;
};

const isScrollableElement = (element: HTMLElement) => {
  const { overflow, overflowY } = window.getComputedStyle(element);

  return (
    SCROLLABLE_OVERFLOW_PATTERN.test(`${overflow} ${overflowY}`) &&
    element.scrollHeight > element.clientHeight
  );
};

const getScrollableAncestor = (
  target?: EventTarget | null,
): HTMLElement | null => {
  let element = getElementFromTarget(target);

  while (element) {
    if (isScrollableElement(element)) {
      return element;
    }

    element = element.parentElement;
  }

  return null;
};

const resolveScrollContainer = (target?: EventTarget | null): ScrollContainer => {
  return getScrollableAncestor(target) ?? 'document';
};

const getDocumentScrollY = () => {
  const scrollElement = getDocumentScrollElement();
  const scrollYValues = [
    window.scrollY,
    scrollElement?.scrollTop,
    document.documentElement.scrollTop,
    document.body.scrollTop,
  ];
  const validScrollYValues = scrollYValues.filter(
    (scrollY): scrollY is number => getValidScrollY(scrollY) !== null,
  );

  return Math.max(0, ...validScrollYValues);
};

const getDocumentMaxScrollY = () => {
  const scrollElement = getDocumentScrollElement();
  const scrollHeight = Math.max(
    scrollElement?.scrollHeight ?? 0,
    document.documentElement.scrollHeight,
    document.body.scrollHeight,
  );

  return Math.max(scrollHeight - window.innerHeight, 0);
};

export const getCurrentScrollY = (target?: EventTarget | null): number => {
  if (typeof window === 'undefined') {
    return 0;
  }

  const scrollContainer = resolveScrollContainer(target);

  if (scrollContainer === 'document') {
    return getDocumentScrollY();
  }

  return scrollContainer.scrollTop;
};

export const getMaxScrollY = (target?: EventTarget | null): number => {
  if (typeof window === 'undefined') {
    return 0;
  }

  const scrollContainer = resolveScrollContainer(target);

  if (scrollContainer === 'document') {
    return getDocumentMaxScrollY();
  }

  return Math.max(scrollContainer.scrollHeight - scrollContainer.clientHeight, 0);
};

export const restoreScrollY = (
  scrollY: number,
  target?: EventTarget | null,
): void => {
  if (typeof window === 'undefined') {
    return;
  }

  const validScrollY = getValidScrollY(scrollY);

  if (validScrollY === null) {
    return;
  }

  const scrollContainer = resolveScrollContainer(target);

  if (scrollContainer === 'document') {
    const scrollElement = getDocumentScrollElement();

    window.scrollTo({
      top: validScrollY,
      behavior: 'auto',
    });

    if (scrollElement) {
      scrollElement.scrollTop = validScrollY;
    }

    return;
  }

  scrollContainer.scrollTop = validScrollY;
};

export const createSessionScrollStorage = (storageKey: string) => {
  const saveScrollY = (source?: ScrollSource): void => {
    if (typeof window === 'undefined') {
      return;
    }

    const scrollY =
      typeof source === 'number'
        ? source
        : getCurrentScrollY(source ?? undefined);
    const validScrollY = getValidScrollY(scrollY);

    if (validScrollY === null) {
      return;
    }

    try {
      window.sessionStorage.setItem(storageKey, String(validScrollY));
    } catch {
      return;
    }
  };

  const getScrollY = (): number | null => {
    if (typeof window === 'undefined') {
      return null;
    }

    try {
      const rawScrollY = window.sessionStorage.getItem(storageKey);

      if (rawScrollY === null) {
        return null;
      }

      return getValidScrollY(Number(rawScrollY));
    } catch {
      return null;
    }
  };

  const clearScrollY = (): void => {
    if (typeof window === 'undefined') {
      return;
    }

    try {
      window.sessionStorage.removeItem(storageKey);
    } catch {
      return;
    }
  };

  return {
    clearScrollY,
    getScrollY,
    saveScrollY,
  };
};
