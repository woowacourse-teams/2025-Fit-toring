export const COMMUNITY_SCROLL_Y_STORAGE_KEY = 'communityScrollY';

export const saveCommunityScrollY = (scrollY: number): void => {
  if (typeof window === 'undefined') {
    return;
  }

  try {
    window.sessionStorage.setItem(
      COMMUNITY_SCROLL_Y_STORAGE_KEY,
      String(scrollY),
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

    if (!Number.isFinite(scrollY) || scrollY < 0) {
      return null;
    }

    return scrollY;
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
