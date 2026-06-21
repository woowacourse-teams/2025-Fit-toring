interface ChannelTalkMemberInfo {
  memberId?: string;
  name: string;
  phoneNumber: string;
}

const PLUGIN_KEY = process.env.CHANNEL_TALK_PLUGIN_KEY ?? '';

export const bootChannelTalk = (memberInfo?: ChannelTalkMemberInfo) => {
  if (!window.ChannelIO) {
    return;
  }

  const bootOption: BootOption = {
    pluginKey: PLUGIN_KEY,
  };

  if (memberInfo) {
    if (memberInfo.memberId) {
      bootOption.memberId = memberInfo.memberId;
    }
    bootOption.profile = {
      name: memberInfo.name,
      mobileNumber: memberInfo.phoneNumber,
    };
  }

  window.ChannelIO('boot', bootOption);
};

export const shutdownChannelTalk = () => {
  if (!window.ChannelIO) {
    return;
  }

  window.ChannelIO('shutdown');
};

export const showChannelTalk = () => {
  if (!window.ChannelIO) {
    return;
  }

  window.ChannelIO('showChannelButton');
};

export const hideChannelTalk = () => {
  if (!window.ChannelIO) {
    return;
  }

  window.ChannelIO('hideChannelButton');
};

export const onHideChannelTalkMessenger = (callback: () => void) => {
  if (!window.ChannelIO) {
    return;
  }

  window.ChannelIO('onHideMessenger', callback);
};

export const clearChannelTalkCallbacks = () => {
  if (!window.ChannelIO) {
    return;
  }

  window.ChannelIO('clearCallbacks');
};
