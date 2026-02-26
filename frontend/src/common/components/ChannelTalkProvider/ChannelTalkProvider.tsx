import type { PropsWithChildren } from 'react';

import useChannelTalk from '../../hooks/useChannelTalk';

function ChannelTalkProvider({ children }: PropsWithChildren) {
  useChannelTalk();

  return <>{children}</>;
}

export default ChannelTalkProvider;
