import { useCallback, useEffect, useMemo, useRef, useState } from 'react';

import {
  fetchFcmToken,
  registerFcmTokenToServer,
  requestPermissionToUser,
  setupForegroundMessageListener,
} from '../../pwa/firebase';
import {
  isIOSPushSupported,
  isNotificationSupported,
  isPWAStandalone,
  isIOS,
} from '../utils/deviceDetection';

const useNotification = (authenticated: boolean) => {
  const [hasClosedModal, setHasClosedModal] = useState(false);

  const isInitializedRef = useRef(false);

  const showModal = useMemo(
    () =>
      authenticated && Notification.permission === 'default' && !hasClosedModal,
    [authenticated, hasClosedModal],
  );

  const closeModal = useCallback(() => {
    setHasClosedModal(true);
  }, []);

  const initializeFcm = useCallback(async () => {
    if (isInitializedRef.current) {
      return;
    }
    isInitializedRef.current = true;

    try {
      await navigator.serviceWorker.ready;

      const fcmToken = await fetchFcmToken();
      if (!fcmToken) {
        throw new Error('FCM 토큰을 가져올 수 없습니다.');
      }

      const memberId = localStorage.getItem('memberId');
      if (memberId) {
        await registerFcmTokenToServer({
          token: fcmToken,
          memberId: Number(memberId),
        });
      }

      setupForegroundMessageListener();
    } catch (error) {
      console.error('[FCM] 초기화 실패:', error);
      throw error;
    }
  }, []);

  const requestNotificationPermission = useCallback(async () => {
    try {
      if (isIOS() && !isPWAStandalone()) {
        setHasClosedModal(true);
        alert(
          '알림을 받으시려면 Safari의 공유 버튼을 눌러 "홈 화면에 추가"를 선택해주세요.',
        );
        return;
      }

      if (!isNotificationSupported()) {
        throw new Error('이 브라우저는 푸시 알림을 지원하지 않습니다.');
      }

      if (isIOS() && !isIOSPushSupported()) {
        alert(
          'iOS 16.4 이상 버전에서만 푸시 알림을 사용할 수 있습니다. 업데이트 해주세요.',
        );
        return;
      }

      const hasPermission = await requestPermissionToUser();

      setHasClosedModal(true);

      if (!hasPermission) {
        alert(
          '알림 권한이 거부되었습니다. 알림을 받으시려면 브라우저에서 권한을 허용해주세요.',
        );
        return;
      }

      await initializeFcm();
    } catch (err) {
      console.error(err);
      setHasClosedModal(true);
      alert('알림 설정 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
  }, [initializeFcm]);

  useEffect(() => {
    if (!authenticated || Notification.permission !== 'granted') {
      return;
    }

    if (isIOS()) {
      if (isPWAStandalone() && isIOSPushSupported()) {
        initializeFcm();
      }
      return;
    }

    initializeFcm();
  }, [authenticated, initializeFcm]);

  return {
    requestNotificationPermission,
    showModal,
    closeModal,
  };
};

export default useNotification;
