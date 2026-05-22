import React from 'react';

import { Global, ThemeProvider } from '@emotion/react';
import * as Sentry from '@sentry/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { createRoot } from 'react-dom/client';
import ReactGA from 'react-ga4';

import App from './App';
import AuthProvider from './common/components/AuthProvider/AuthProvider';
import { resetCss } from './common/styles/reset';
import { THEME } from './common/styles/theme';
import { registerServiceWorker } from './pwa/serviceWorker';

Sentry.init({
  dsn: process.env.SENTRY_DSN,
  environment: process.env.NODE_ENV,
  sendDefaultPii: true,
  tracesSampleRate: 1.0,
  integrations: [
    Sentry.browserTracingIntegration(),
    Sentry.replayIntegration(),
  ],
  tracePropagationTargets: [
    'localhost',
    'api.fittoring.com',
    'devapi.fittoring.com',
  ],
  replaysSessionSampleRate: 0.1,
  replaysOnErrorSampleRate: 1.0,
});

const queryClient = new QueryClient();

function preventPwaZoom() {
  const preventDefault = (event: Event) => {
    event.preventDefault();
  };

  const preventMultiTouchZoom = (event: TouchEvent) => {
    if (event.touches.length > 1) {
      event.preventDefault();
    }
  };

  // Safari 계열 비표준 gesture 이벤트로 iOS PWA 핀치 줌을 차단합니다.
  document.addEventListener('gesturestart', preventDefault, {
    passive: false,
  });
  document.addEventListener('gesturechange', preventDefault, {
    passive: false,
  });
  document.addEventListener('gestureend', preventDefault, {
    passive: false,
  });
  document.addEventListener('touchmove', preventMultiTouchZoom, {
    passive: false,
  });
}

async function enableMocking() {
  // 사용시 주석 제거
  // const { worker } = await import('./common/mock/browser');
  // const isLocalHost = process.env.NODE_ENV === 'development';
  // if (!isLocalHost) {
  //   return;
  // }
  // return worker.start();
}

ReactGA.initialize(`${process.env.GOOGLE_ANALYTICS_ID}`);

(async () => {
  const isProd = process.env.NODE_ENV === 'production';

  if (!isProd) {
    await enableMocking();
  }

  preventPwaZoom();

  createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
      <ThemeProvider theme={THEME}>
        <QueryClientProvider client={queryClient}>
          <AuthProvider>
            <Global styles={[resetCss]} />
            <App />
          </AuthProvider>
        </QueryClientProvider>
      </ThemeProvider>
    </React.StrictMode>,
  );

  registerServiceWorker();
})();
