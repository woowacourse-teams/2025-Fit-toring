import React from 'react';

import { ThemeProvider, Global } from '@emotion/react';
import { createRoot } from 'react-dom/client';

import App from './App';
import AuthProvider from './common/components/AuthProvider/AuthProvider';
import { fonts } from './common/styles/fonts';
import { resetCss } from './common/styles/reset';
import { THEME } from './common/styles/theme';

async function enableMocking() {
  return; // 사용 시 return 제거
}

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
