export function isIOS(): boolean {
  const userAgent = window.navigator.userAgent.toLowerCase();
  const isIPhoneIPadIPod = /iphone|ipad|ipod/.test(userAgent);

  const isIPadDesktopUA =
    navigator.platform === 'MacIntel' && navigator.maxTouchPoints > 1;

  return isIPhoneIPadIPod || isIPadDesktopUA;
}

export function getIOSVersion(): number | null {
  const match = window.navigator.userAgent.match(/OS (\d+)_(\d+)/);
  if (match) {
    const major = parseInt(match[1], 10);
    const minor = parseInt(match[2], 10);
    return major + minor / 10;
  }

  return null;
}

export function isIOSPushSupported(): boolean {
  const IOS_PUSH_MIN_VERSION = 16.4;
  const version = getIOSVersion();

  if (version === null) {
    return false;
  }

  return version >= IOS_PUSH_MIN_VERSION;
}

export function isPWAStandalone(): boolean {
  return window.matchMedia('(display-mode: standalone)').matches;
}

export function isNotificationSupported(): boolean {
  return (
    'Notification' in window &&
    'serviceWorker' in navigator &&
    'PushManager' in window
  );
}
