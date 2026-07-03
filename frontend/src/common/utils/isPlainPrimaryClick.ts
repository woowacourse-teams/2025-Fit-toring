import type { MouseEvent } from 'react';

export const isPlainPrimaryClick = (event: MouseEvent<HTMLElement>) => {
  if (
    event.metaKey ||
    event.ctrlKey ||
    event.shiftKey ||
    event.altKey ||
    event.button !== 0
  ) {
    return false;
  }

  return !(
    event.currentTarget instanceof HTMLAnchorElement &&
    event.currentTarget.target === '_blank'
  );
};
