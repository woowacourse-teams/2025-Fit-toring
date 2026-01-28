/// <reference lib="WebWorker" />
import { initializeApp } from 'firebase/app';
import { onBackgroundMessage, getMessaging } from 'firebase/messaging/sw';
import { clientsClaim, skipWaiting } from 'workbox-core';
import { cleanupOutdatedCaches, precacheAndRoute } from 'workbox-precaching';

import { PAGE_URL } from '../common/constants/url';

declare let self: ServiceWorkerGlobalScope;

clientsClaim();
skipWaiting();
cleanupOutdatedCaches();
precacheAndRoute(self.__WB_MANIFEST);

const firebaseConfig = {
  apiKey: 'AIzaSyCbmcTDZNommWF5IJjrSSD8An7OdNROewA',
  authDomain: 'fittoring.firebaseapp.com',
  projectId: 'fittoring',
  storageBucket: 'fittoring.firebasestorage.app',
  messagingSenderId: '948904127890',
  appId: '1:948904127890:web:6c40f6a464d523bc9ba54b',
};

const app = initializeApp(firebaseConfig);

const messaging = getMessaging(app);

onBackgroundMessage(messaging, (payload) => {
  const iconPath = `${self.location.origin}/fittoring-icon-192.png`;

  const data = payload.data || {};

  const notificationTitle = data.title || '제목 없음';
  const notificationOptions = {
    body: '백그라운드 알림: ' + data.body || '내용 없음',
    icon: iconPath,
    badge: iconPath,
    data: {
      chatRoomId: data.chatRoomId,
    },
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});

const getChatRoomURL = (roomId: string) => {
  return `${self.location.origin}${PAGE_URL.CHAT_ROOM}/${roomId}`;
};

self.addEventListener('notificationclick', (e) => {
  const notification = e.notification;
  notification.close();

  if (!notification.data) {
    return;
  }

  const chatRoomId = notification.data.chatRoomId;
  if (!chatRoomId) {
    return;
  }

  const chatRoomURL = getChatRoomURL(chatRoomId);

  e.waitUntil(
    self.clients
      .matchAll({ type: 'window', includeUncontrolled: true })
      .then((windowClients) => {
        for (const client of windowClients) {
          if (
            client.url.startsWith(self.location.origin) &&
            'focus' in client
          ) {
            return client.focus().then(() => client.navigate(chatRoomURL));
          }
        }

        if (self.clients.openWindow) {
          return self.clients.openWindow(chatRoomURL);
        }
      }),
  );
});
