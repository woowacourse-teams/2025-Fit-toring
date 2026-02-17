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
    if (authenticated) {
      const memberId = localStorage.getItem('memberId');

      getUserInfoSummary()
        .then((userInfo) => {
          bootChannelTalk({
            memberId: memberId ?? '',
            name: userInfo.name,
            phoneNumber: userInfo.phoneNumber,
          });
        })
        .catch(() => {
          bootChannelTalk();
        });
    } else {
      bootChannelTalk();
    }

    return () => {
      shutdownChannelTalk();
    };
  }, [authenticated]);
};

export default useChannelTalk;
