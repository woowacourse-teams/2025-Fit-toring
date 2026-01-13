import { useEffect, useRef } from 'react';

import {
  fetchFcmToken,
  registerFcmTokenToServer,
  requestPermissionToUser,
  setupForegroundMessageListener,
} from '../../pwa/firebase';

const useInitializeFcm = () => {
  const isInitialized = useRef(false);
  const unsubscribeRef = useRef<(() => void) | null>(null);

  useEffect(() => {
    const memberId = localStorage.getItem('memberId');

    if (isInitialized.current || !memberId) {
      return;
    }
    isInitialized.current = true;

    async function initializeFcm() {
      try {
        const permission = await requestPermissionToUser();
        if (!permission) {
          alert(
            '채팅 알림 권한이 거부되었습니다. 채팅 알림을 받으시려면 브라우저에서 권한을 허용해주세요.',
          );
          return;
        }

        await navigator.serviceWorker.ready;

        const currentToken = await fetchFcmToken();
        if (!currentToken) {
          return;
        }

        await registerFcmTokenToServer({
          token: currentToken,
          memberId: Number(memberId),
        });

        unsubscribeRef.current = setupForegroundMessageListener();
      } catch (error) {
        console.error('FCM 초기화 중 오류 발생:', error);
        isInitialized.current = false;
      }
    }

    initializeFcm();

    return () => {
      if (!isInitialized.current) {
        return;
      }

      unsubscribeRef.current?.();
    };
  }, []);
};

export default useInitializeFcm;
