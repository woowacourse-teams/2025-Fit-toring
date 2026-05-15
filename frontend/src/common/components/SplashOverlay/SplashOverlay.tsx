import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import { isIOS, isPWAStandalone } from '../../utils/deviceDetection';

const SPLASH_SESSION_KEY = 'fittoring:splash-overlay-shown';
const SPLASH_DURATION_MS = 1000;
const SPLASH_FADE_MS = 180;

function hasSplashOverlayShown() {
  try {
    return sessionStorage.getItem(SPLASH_SESSION_KEY) === 'true';
  } catch {
    return false;
  }
}

function markSplashOverlayShown() {
  try {
    sessionStorage.setItem(SPLASH_SESSION_KEY, 'true');
  } catch {
    return;
  }
}

function shouldShowSplashOverlay() {
  return isPWAStandalone() && !isIOS() && !hasSplashOverlayShown();
}

function getSplashFadeMs() {
  const prefersReducedMotion = window.matchMedia(
    '(prefers-reduced-motion: reduce)',
  ).matches;

  return prefersReducedMotion ? 0 : SPLASH_FADE_MS;
}

function SplashOverlay() {
  const [visible, setVisible] = useState(false);
  const [closing, setClosing] = useState(false);
  const [fadeMs, setFadeMs] = useState(SPLASH_FADE_MS);

  useEffect(() => {
    if (!shouldShowSplashOverlay()) {
      return;
    }

    const nextFadeMs = getSplashFadeMs();

    setFadeMs(nextFadeMs);
    setVisible(true);

    const fadeTimer = window.setTimeout(() => {
      setClosing(true);
    }, SPLASH_DURATION_MS);

    const hideTimer = window.setTimeout(() => {
      markSplashOverlayShown();
      setVisible(false);
    }, SPLASH_DURATION_MS + nextFadeMs);

    return () => {
      window.clearTimeout(fadeTimer);
      window.clearTimeout(hideTimer);
    };
  }, []);

  if (!visible) {
    return null;
  }

  return (
    <S_Overlay $closing={closing} $fadeMs={fadeMs} aria-hidden="true">
      <S_SplashImage src="/splash/fittoring-splash.png" alt="" />
    </S_Overlay>
  );
}

export default SplashOverlay;

const S_Overlay = styled.div<{ $closing: boolean; $fadeMs: number }>`
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  opacity: ${({ $closing }) => ($closing ? 0 : 1)};
  transition: opacity ${({ $fadeMs }) => $fadeMs}ms ease-out;
  pointer-events: ${({ $closing }) => ($closing ? 'none' : 'auto')};
`;

const S_SplashImage = styled.img`
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center center;

  @media (orientation: landscape) {
    object-fit: contain;
  }
`;
