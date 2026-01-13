import { captureSentryError } from '../common/utils/captureSentryError';

export function registerServiceWorker() {
  if (!('serviceWorker' in navigator)) {
    return;
  }

  window.addEventListener('load', () => {
    navigator.serviceWorker
      .register('/firebase-messaging-sw.js')
      .catch((error) => {
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

export async function cleanupServiceWorkerInDev() {
  if (!('serviceWorker' in navigator)) {
    return;
  }
  if (process.env.NODE_ENV !== 'development') {
    return;
  }

  const registrations = await navigator.serviceWorker.getRegistrations();
  await Promise.all(registrations.map((reg) => reg.unregister()));
}
