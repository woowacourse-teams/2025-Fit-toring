/// <reference lib="WebWorker" />
import { initializeApp } from 'firebase/app';
import { onBackgroundMessage, getMessaging } from 'firebase/messaging/sw';
import { cleanupOutdatedCaches, precacheAndRoute } from 'workbox-precaching';

import { PAGE_URL } from '../common/constants/url';

declare let self: ServiceWorkerGlobalScope;

cleanupOutdatedCaches();
precacheAndRoute(self.__WB_MANIFEST);

self.addEventListener('install', (e) => {
  e.waitUntil(self.skipWaiting());
});

self.addEventListener('activate', (e) => {
  e.waitUntil(self.clients.claim());
});

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
    body: data.body || '내용 없음',
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

  const chatRoomURL = chatRoomId
    ? getChatRoomURL(chatRoomId)
    : self.location.origin;

  e.waitUntil(
    self.clients
      .matchAll({
        type: 'window',
        includeUncontrolled: false,
      })
      .then((clientList) => {
        for (const client of clientList) {
          if (
            client.url.startsWith(self.location.origin) &&
            'focus' in client
          ) {
            return client.focus().then((focusedClient) => {
              return focusedClient.navigate(chatRoomURL);
            });
          }
        }

        if (self.clients.openWindow) {
          return self.clients.openWindow(chatRoomURL);
        }
      }),
  );
});
