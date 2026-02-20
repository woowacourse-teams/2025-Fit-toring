import { useEffect } from 'react';

import {
  fetchFcmToken,
  registerFcmTokenToServer,
  requestPermissionToUser,
  setupForegroundMessageListener,
} from '../../pwa/firebase';
import { isIOS } from '../utils/deviceDetection';

const useInitializeFcm = () => {
  useEffect(() => {
    const memberId = localStorage.getItem('memberId');

    if (isIOS() || !memberId) {
      return;
    }

    let isInitialized = false;

    async function initializeFcm() {
      try {
        const permission = await requestPermissionToUser();
        if (isInitialized) {
          return;
        }

        if (!permission) {
          alert(
            '채팅 알림 권한이 거부되었습니다. 채팅 알림을 받으시려면 브라우저에서 권한을 허용해주세요.',
          );
          return;
        }

        await navigator.serviceWorker.ready;
        if (isInitialized) {
          return;
        }

        const currentToken = await fetchFcmToken();
        if (!currentToken || isInitialized) {
          return;
        }

        await registerFcmTokenToServer({
          token: currentToken,
          memberId: Number(memberId),
        });

        if (isInitialized) {
          return;
        }

        setupForegroundMessageListener();
      } catch (error) {
        console.error('FCM 초기화 중 오류 발생:', error);
      }
    }

    initializeFcm();

    return () => {
      isInitialized = true;
    };
  }, []);
};

export default useInitializeFcm;
