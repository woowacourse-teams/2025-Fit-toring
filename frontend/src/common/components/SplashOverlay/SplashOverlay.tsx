import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import { isPWAStandalone } from '../../utils/deviceDetection';

const SPLASH_SESSION_KEY = 'fittoring:splash-overlay-shown';
const SPLASH_DURATION_MS = 3100;
const SPLASH_FADE_MS = 220;

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
  const prefersReducedMotion = window.matchMedia(
    '(prefers-reduced-motion: reduce)',
  ).matches;

  return isPWAStandalone() && !prefersReducedMotion && !hasSplashOverlayShown();
}

function SplashOverlay() {
  const [visible, setVisible] = useState(false);
  const [closing, setClosing] = useState(false);

  useEffect(() => {
    if (!shouldShowSplashOverlay()) {
      return;
    }

    setVisible(true);

    const fadeTimer = window.setTimeout(() => {
      setClosing(true);
    }, SPLASH_DURATION_MS);

    const hideTimer = window.setTimeout(() => {
      markSplashOverlayShown();
      setVisible(false);
    }, SPLASH_DURATION_MS + SPLASH_FADE_MS);

    return () => {
      window.clearTimeout(fadeTimer);
      window.clearTimeout(hideTimer);
    };
  }, []);

  if (!visible) {
    return null;
  }

  return (
    <S_Overlay $closing={closing} aria-hidden="true">
      <S_SplashImage src="/splash/fittoring-splash.gif" alt="" />
    </S_Overlay>
  );
}

export default SplashOverlay;

const S_Overlay = styled.div<{ $closing: boolean }>`
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  opacity: ${({ $closing }) => ($closing ? 0 : 1)};
  transition: opacity ${SPLASH_FADE_MS}ms ease-out;
  pointer-events: ${({ $closing }) => ($closing ? 'none' : 'auto')};
`;

const S_SplashImage = styled.img`
  width: 100%;
  height: 100%;
  object-fit: contain;
`;
