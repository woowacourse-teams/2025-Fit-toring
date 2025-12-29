import { captureSentryError } from '../common/utils/captureSentryError';

export function registerServiceWorker() {
  if (!('serviceWorker' in navigator)) {
    return;
  }
  if (process.env.NODE_ENV !== 'production') {
    return;
  }

  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js').catch((error) => {
      if (process.env.NODE_ENV === 'production') {
        captureSentryError({
          error,
          level: 'warning',
          feature: 'pwa',
          step: 'service-worker',
        });
      } else {
        console.error(error);
      }
    });
  });
}
