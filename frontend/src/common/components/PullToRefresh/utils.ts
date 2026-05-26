import { isMobileViewport, isPWAStandalone } from '../../utils/deviceDetection';

export const isPullToRefreshEnabled = () => {
  return (
    isPWAStandalone() &&
    isMobileViewport() &&
    window.navigator.maxTouchPoints > 0
  );
};
