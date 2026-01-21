export function isIOS(): boolean {
  const userAgent = window.navigator.userAgent.toLowerCase();
  return /iphone|ipad|ipod/.test(userAgent);
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
