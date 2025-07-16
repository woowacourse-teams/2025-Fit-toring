import React from 'react';

import { ThemeProvider, Global } from '@emotion/react';
import { createRoot } from 'react-dom/client';

import App from './App';
import { fonts } from './common/styles/fonts';
import { resetCss } from './common/styles/reset';
import { THEME } from './common/styles/theme';

createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ThemeProvider theme={THEME}>
      <Global styles={[resetCss, fonts]} />
      <App />
    </ThemeProvider>
  </React.StrictMode>,
);
