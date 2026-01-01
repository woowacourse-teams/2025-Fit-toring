import { getMessaging, getToken, onMessage } from 'firebase/messaging';

import { firebaseApp } from './firebaseApp';

import type { MessagePayload } from 'firebase/messaging';

export const messaging = getMessaging(firebaseApp);

export async function requestFCMToken() {
  const permission = await Notification.requestPermission();
  if (permission !== 'granted') {
    return null;
  }

  return getToken(messaging, {
    vapidKey: process.env.FIREBASE_VAPID_KEY,
  });
}

export function onForegroundMessage(
  callback: (payload: MessagePayload) => void,
) {
  onMessage(messaging, callback);
}
