import { useEffect } from 'react';

import { getUserInfoSummary } from '../apis/getUserInfoSummary';
import { useAuth } from '../components/AuthProvider/AuthProvider';
import {
  bootChannelTalk,
  shutdownChannelTalk,
} from '../utils/channelTalk';

const useChannelTalk = () => {
  const { authenticated } = useAuth();

  useEffect(() => {
    let ignore = false;

    if (authenticated) {
      const memberId = localStorage.getItem('memberId');

      getUserInfoSummary()
        .then((userInfo) => {
          if (ignore) return;
          bootChannelTalk({
            memberId: memberId ?? undefined,
            name: userInfo.name,
            phoneNumber: userInfo.phoneNumber,
          });
        })
        .catch(() => {
          if (ignore) return;
          bootChannelTalk();
        });
    } else {
      bootChannelTalk();
    }

    return () => {
      ignore = true;
      shutdownChannelTalk();
    };
  }, [authenticated]);
};

export default useChannelTalk;
