import { initializeApp } from 'firebase/app';
import { getMessaging, getToken, onMessage } from 'firebase/messaging';

import { apiClient } from '../common/apis/apiClient';
import { API_ENDPOINTS } from '../common/constants/apiEndpoints';

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

export async function requestPermissionToUser() {
  const permission = await Notification.requestPermission();

  if (permission === 'granted') {
    return true;
  } else if (permission === 'denied') {
    return false;
  }
}

export async function fetchFcmToken() {
  const currentToken = await getToken(messaging, {
    vapidKey:
      'BLkcntwvB-0yrGk9KF2Of72t7omeI1a8E5pL5KSxroG7yhSGE59JvUhMVZ0qZ3750u1P5cvUnSpg_TfKhbFcaRk',
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
