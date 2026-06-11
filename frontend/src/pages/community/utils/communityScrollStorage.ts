import {
  createSessionScrollStorage,
  getCurrentScrollY,
  getMaxScrollY,
  restoreScrollY,
} from '../../../common/utils/scrollRestoration';

import type { ScrollSource } from '../../../common/utils/scrollRestoration';

export const COMMUNITY_SCROLL_Y_STORAGE_KEY = 'communityScrollY';

const communityScrollStorage = createSessionScrollStorage(
  COMMUNITY_SCROLL_Y_STORAGE_KEY,
);

export const getCurrentCommunityScrollY = getCurrentScrollY;
export const getMaxCommunityScrollY = getMaxScrollY;
export const restoreCommunityScrollY = restoreScrollY;

export const saveCommunityScrollY = (source?: ScrollSource): void => {
  communityScrollStorage.saveScrollY(source);
};

export const getCommunityScrollY = (): number | null => {
  return communityScrollStorage.getScrollY();
};

export const clearCommunityScrollY = (): void => {
  communityScrollStorage.clearScrollY();
};
