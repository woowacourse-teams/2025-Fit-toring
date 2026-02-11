import { useCallback, useEffect, useState } from 'react';

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
  const [showModal, setShowModal] = useState(() => {
    return (
      authenticated &&
      isIOS() &&
      isPWAStandalone() &&
      isIOSPushSupported() &&
      Notification.permission === 'default'
    );
  });

  const closeModal = useCallback(() => {
    setShowModal(false);
  }, []);

  const requestNotificationPermission = useCallback(async () => {
    try {
      if (!isNotificationSupported()) {
        throw new Error('이 브라우저는 푸시 알림을 지원하지 않습니다.');
      }

      if (isIOS() && !isIOSPushSupported()) {
        throw new Error(
          'iOS 16.4 이상 버전에서만 푸시 알림을 사용할 수 있습니다.',
        );
      }

      const hasPermission = await requestPermissionToUser();

      setShowModal(false);

      if (!hasPermission) {
        alert(
          '알림 권한이 거부되었습니다. 알림을 받으시려면 브라우저에서 권한을 허용해주세요.',
        );
        return;
      }

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
    } catch (err) {
      console.error(err);
      setShowModal(false);
      alert('알림 설정 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
  }, []);

  useEffect(() => {
    if (!authenticated) {
      return;
    }

    async function initializeFcmForIOS() {
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
    }

    if (
      isIOS() &&
      isPWAStandalone() &&
      isIOSPushSupported() &&
      Notification.permission === 'granted'
    ) {
      initializeFcmForIOS();
    }
  }, [authenticated]);

  return {
    requestNotificationPermission,
    showModal,
    closeModal,
  };
};

export default useNotification;
