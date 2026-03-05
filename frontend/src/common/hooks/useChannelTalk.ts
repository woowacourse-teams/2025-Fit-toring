import { useEffect, useRef } from 'react';

import { matchPath, useLocation } from 'react-router-dom';

import { getUserInfoSummary } from '../apis/getUserInfoSummary';
import { useAuth } from '../components/AuthProvider/AuthProvider';
import { PAGE_URL } from '../constants/url';
import {
  bootChannelTalk,
  hideChannelTalk,
  showChannelTalk,
} from '../utils/channelTalk';

const HIDDEN_PATHS = [`${PAGE_URL.CHAT_ROOM}/:chatRoomId`];

const useChannelTalk = () => {
  const { authenticated } = useAuth();
  const { pathname } = useLocation();

  const isHidden = HIDDEN_PATHS.some(
    (path) => !!matchPath({ path, end: true }, pathname),
  );

  const bootedRef = useRef(false);

  useEffect(() => {
    if (isHidden) {
      return;
    }

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
  }, [isHidden, authenticated]);

  useEffect(() => {
    if (isHidden) {
      hideChannelTalk();
    } else {
      showChannelTalk();
    }
  }, [isHidden]);
};

export default useChannelTalk;
