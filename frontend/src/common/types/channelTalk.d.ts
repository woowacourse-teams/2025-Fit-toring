interface Profile {
  name?: string | null;
  email?: string | null;
  mobileNumber?: string | null;
}

interface BootOption {
  pluginKey: string;
  memberId?: string;
  memberHash?: string;
  profile?: Profile;
  language?: string;
  trackDefaultEvent?: boolean;
  trackUtmSource?: boolean;
  unsubscribeEmail?: boolean;
  unsubscribeTexting?: boolean;
  hideChannelButtonOnBoot?: boolean;
  hidePopup?: boolean;
  zIndex?: number;
  customLauncherSelector?: string;
  appearance?: string;
}

interface ChannelIOStatic {
  (...args: unknown[]): void;
  q?: unknown[];
  c?: (args: unknown) => void;
}

interface Window {
  ChannelIO?: ChannelIOStatic;
  ChannelIOInitialized?: boolean;
}
