interface ChannelIOStatic {
  (...args: unknown[]): void;
  q?: unknown[];
  c?: (args: unknown) => void;
}

interface Window {
  ChannelIO?: ChannelIOStatic;
  ChannelIOInitialized?: boolean;
}
