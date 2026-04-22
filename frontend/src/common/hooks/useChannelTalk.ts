import { useEffect, useRef } from 'react';

import { getUserInfoSummary } from '../apis/getUserInfoSummary';
import { useAuth } from '../components/AuthProvider/AuthProvider';
import {
  bootChannelTalk,
  shutdownChannelTalk,
  showChannelTalk,
} from '../utils/channelTalk';

const useChannelTalk = () => {
  const { authenticated } = useAuth();

  const bootedRef = useRef(false);

  useEffect(() => {
    return () => {
      shutdownChannelTalk();
      bootedRef.current = false;
    };
  }, []);

  useEffect(() => {
    if (bootedRef.current) {
      return;
    }

    let ignore = false;

    const boot = (params?: Parameters<typeof bootChannelTalk>[0]) => {
      if (ignore) {
        return;
      }
      bootChannelTalk(params);

      bootedRef.current = true;

      showChannelTalk();
    };

    if (authenticated) {
      const memberId = localStorage.getItem('memberId');

      if (!memberId) {
        boot();
      } else {
        getUserInfoSummary()
          .then((userInfo) => {
            boot({
              memberId,
              name: userInfo.name,
              phoneNumber: userInfo.phoneNumber,
            });
          })
          .catch(() => {
            boot();
          });
      }
    } else {
      boot();
    }

    return () => {
      ignore = true;
    };
  }, [authenticated]);
};

export default useChannelTalk;
