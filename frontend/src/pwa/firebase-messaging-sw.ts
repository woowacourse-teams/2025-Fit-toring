/// <reference lib="WebWorker" />
import { initializeApp } from 'firebase/app';
import { onBackgroundMessage, getMessaging } from 'firebase/messaging/sw';
import { clientsClaim, skipWaiting } from 'workbox-core';
import { cleanupOutdatedCaches, precacheAndRoute } from 'workbox-precaching';

declare let self: ServiceWorkerGlobalScope;

clientsClaim();
skipWaiting();
cleanupOutdatedCaches();
precacheAndRoute(self.__WB_MANIFEST);

const firebaseConfig = {
  apiKey: 'AIzaSyCrUhONRlAeig6WSRTrvwZBPYjIeTiC5sA',
  authDomain: 'fittoring-dev.firebaseapp.com',
  projectId: 'fittoring-dev',
  storageBucket: 'fittoring-dev.firebasestorage.app',
  messagingSenderId: '263030224988',
  appId: '1:263030224988:web:0d8da83df7e01155b0ce99',
};

const app = initializeApp(firebaseConfig);

const messaging = getMessaging(app);

onBackgroundMessage(messaging, (payload) => {
  const iconPath = `${self.location.origin}/fittoring-icon-192.png`;

  const notificationTitle = payload.notification?.title || '제목 없음';
  const notificationOptions = {
    body: payload.notification?.body || '내용 없음',
    icon: iconPath,
    badge: iconPath,
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});
