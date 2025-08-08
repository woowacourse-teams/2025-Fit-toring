import React from 'react';

import { ThemeProvider, Global } from '@emotion/react';
import * as Sentry from '@sentry/react';
import { createRoot } from 'react-dom/client';

import App from './App';
import AuthProvider from './common/components/AuthProvider/AuthProvider';
import { fonts } from './common/styles/fonts';
import { resetCss } from './common/styles/reset';
import { THEME } from './common/styles/theme';
import ReactGA from 'react-ga4';

Sentry.init({
  dsn: process.env.SENTRY_DSN,
  sendDefaultPii: true,
  tracesSampleRate: 1.0,
  integrations: [
    Sentry.browserTracingIntegration(),
    Sentry.replayIntegration(),
  ],
  tracePropagationTargets: ['localhost', /^https:/, /yourserver\.io\/api/],
  replaysSessionSampleRate: 0.1,
  replaysOnErrorSampleRate: 1.0,
});

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

enableMocking().then(() => {
  createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
      <ThemeProvider theme={THEME}>
        <AuthProvider>
          <Global styles={[resetCss, fonts]} />
          <App />
        </AuthProvider>
      </ThemeProvider>
    </React.StrictMode>,
  );
});
