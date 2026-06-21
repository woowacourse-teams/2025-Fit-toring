import {
  createSessionScrollStorage,
  getMaxScrollY,
  restoreScrollY,
} from '../../../common/utils/scrollRestoration';

import type { ScrollSource } from '../../../common/utils/scrollRestoration';

export const HOME_SCROLL_Y_STORAGE_KEY = 'homeScrollY';

const homeScrollStorage = createSessionScrollStorage(HOME_SCROLL_Y_STORAGE_KEY);

export const getMaxHomeScrollY = getMaxScrollY;
export const restoreHomeScrollY = restoreScrollY;

export const saveHomeScrollY = (source?: ScrollSource): void => {
  homeScrollStorage.saveScrollY(source);
};

export const getHomeScrollY = (): number | null => {
  return homeScrollStorage.getScrollY();
};

export const clearHomeScrollY = (): void => {
  homeScrollStorage.clearScrollY();
};
