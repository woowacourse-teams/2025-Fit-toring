import { useCallback, useState } from 'react';

import {
  fetchFcmToken,
  registerFcmTokenToServer,
  requestPermissionToUser,
} from '../../pwa/firebase';
import {
  isIOSPushSupported,
  isNotificationSupported,
  isPWAStandalone,
  isIOS,
} from '../utils/deviceDetection';

const useNotification = () => {
  const [showModal, setShowModal] = useState(() => {
    return (
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

      if (!hasPermission) {
        setShowModal(false);
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

      setShowModal(false);
    } catch (err) {
      console.error(err);
    }
  }, []);

  return {
    requestNotificationPermission,
    showModal,
    closeModal,
  };
};

export default useNotification;
