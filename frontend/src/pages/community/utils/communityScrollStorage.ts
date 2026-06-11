export const COMMUNITY_SCROLL_Y_STORAGE_KEY = 'communityScrollY';

type ScrollSource = number | EventTarget | null;

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

const getDocumentScrollYValues = () => [
  window.scrollY,
  window.pageYOffset,
  document.scrollingElement?.scrollTop,
  document.documentElement.scrollTop,
  document.body.scrollTop,
];

const getAncestorScrollYValues = (target?: EventTarget | null) => {
  const scrollYValues: number[] = [];
  let element = getElementFromTarget(target);

  while (element) {
    scrollYValues.push(element.scrollTop);
    element = element.parentElement;
  }

  return scrollYValues;
};

const getDocumentMaxScrollY = () => {
  const scrollHeight = Math.max(
    document.scrollingElement?.scrollHeight ?? 0,
    document.documentElement.scrollHeight,
    document.body.scrollHeight,
  );

  return Math.max(scrollHeight - window.innerHeight, 0);
};

const getAncestorMaxScrollYValues = (target?: EventTarget | null) => {
  const maxScrollYValues: number[] = [];
  let element = getElementFromTarget(target);

  while (element) {
    maxScrollYValues.push(
      Math.max(element.scrollHeight - element.clientHeight, 0),
    );
    element = element.parentElement;
  }

  return maxScrollYValues;
};

export const getCurrentCommunityScrollY = (
  target?: EventTarget | null,
): number => {
  if (typeof window === 'undefined') {
    return 0;
  }

  const scrollYValues = [
    ...getDocumentScrollYValues(),
    ...getAncestorScrollYValues(target),
  ];
  const validScrollYValues = scrollYValues.filter(
    (scrollY): scrollY is number => getValidScrollY(scrollY) !== null,
  );

  return Math.max(0, ...validScrollYValues);
};

export const getMaxCommunityScrollY = (target?: EventTarget | null): number => {
  if (typeof window === 'undefined') {
    return 0;
  }

  return Math.max(
    getDocumentMaxScrollY(),
    ...getAncestorMaxScrollYValues(target),
  );
};

export const restoreCommunityScrollY = (
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

  window.scrollTo({
    top: validScrollY,
    behavior: 'auto',
  });

  if (document.scrollingElement) {
    document.scrollingElement.scrollTop = validScrollY;
  }

  document.documentElement.scrollTop = validScrollY;
  document.body.scrollTop = validScrollY;

  let element = getElementFromTarget(target);

  while (element) {
    element.scrollTop = validScrollY;
    element = element.parentElement;
  }
};

export const saveCommunityScrollY = (
  source?: ScrollSource,
): void => {
  if (typeof window === 'undefined') {
    return;
  }

  const scrollY =
    typeof source === 'number'
      ? source
      : getCurrentCommunityScrollY(source ?? undefined);
  const validScrollY = getValidScrollY(scrollY);

  if (validScrollY === null) {
    return;
  }

  try {
    window.sessionStorage.setItem(
      COMMUNITY_SCROLL_Y_STORAGE_KEY,
      String(validScrollY),
    );
  } catch {
    return;
  }
};

export const getCommunityScrollY = (): number | null => {
  if (typeof window === 'undefined') {
    return null;
  }

  try {
    const rawScrollY = window.sessionStorage.getItem(
      COMMUNITY_SCROLL_Y_STORAGE_KEY,
    );

    if (rawScrollY === null) {
      return null;
    }

    const scrollY = Number(rawScrollY);
    const validScrollY = getValidScrollY(scrollY);

    if (validScrollY === null) {
      return null;
    }

    return validScrollY;
  } catch {
    return null;
  }
};

export const clearCommunityScrollY = (): void => {
  if (typeof window === 'undefined') {
    return;
  }

  try {
    window.sessionStorage.removeItem(COMMUNITY_SCROLL_Y_STORAGE_KEY);
  } catch {
    return;
  }
};
