import { Outlet } from 'react-router-dom';

import useChannelTalk from '../../hooks/useChannelTalk';

function ChannelTalkProvider() {
  useChannelTalk();

  return <Outlet />;
}

export default ChannelTalkProvider;
