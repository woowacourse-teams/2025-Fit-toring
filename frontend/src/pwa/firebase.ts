import { initializeApp } from 'firebase/app';
import { getMessaging, getToken, onMessage } from 'firebase/messaging';

import { apiClient } from '../common/apis/apiClient';
import { API_ENDPOINTS } from '../common/constants/apiEndpoints';
import { PAGE_URL } from '../common/constants/url';

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

export async function requestPermissionToUser() {
  const permission = await Notification.requestPermission();

  if (permission === 'granted') {
    return true;
  } else if (permission === 'denied') {
    return false;
  }
}

export async function fetchFcmToken() {
  const existingRegistration = await navigator.serviceWorker.getRegistration();

  if (!existingRegistration) {
    console.error('Service Worker가 등록되지 않았습니다');
    return null;
  }

  const registration = await navigator.serviceWorker.ready;

  const currentToken = await getToken(messaging, {
    vapidKey:
      'BBsK6Sa5h6aa286kwoR4wFWSeV3eik4UO42zsQ9tIcOpSPFJPuP16LxreaFTm6t5wI50-ct7IAlYAD4zw3ta_6A',
    serviceWorkerRegistration: registration,
  });

  if (currentToken) {
    return currentToken;
  } else {
    return null;
  }
}

export async function registerFcmTokenToServer({
  token,
  memberId,
}: {
  token: string;
  memberId: number;
}) {
  await apiClient.post({
    endpoint: API_ENDPOINTS.FCM_TOKENS,
    body: {
      pushToken: token,
      memberId,
    },
  });
}

export function setupForegroundMessageListener() {
  return onMessage(messaging, (payload) => {
    if (!payload.data || Notification.permission !== 'granted') {
      return;
    }

    const chatRoomId = payload.data.chatRoomId;

    const currentChatRoomURL = chatRoomId
      ? `${PAGE_URL.CHAT_ROOM}/${chatRoomId}`
      : '/';

    if (chatRoomId && window.location.pathname === currentChatRoomURL) {
      return;
    }

    const iconPath = '/fittoring-icon-192.png';
    const notificationTitle = payload.data.title || '제목 없음';
    const notificationOptions = {
      body: payload.data.body || '내용 없음',
      icon: iconPath,
      badge: iconPath,
      data: {
        chatRoomId,
      },
    };

    const notification = new Notification(
      notificationTitle,
      notificationOptions,
    );

    notification.onclick = (e) => {
      e.preventDefault();
      notification.close();

      window.location.href = currentChatRoomURL;
    };
  });
}
