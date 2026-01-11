import { initializeApp } from 'firebase/app';
import { getMessaging, getToken, onMessage } from 'firebase/messaging';
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
