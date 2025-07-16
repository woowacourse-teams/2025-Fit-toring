import { ThemeProvider, Global } from '@emotion/react';
import { resetCss } from './reset';
import { THEME } from './theme';
import React from 'react';

import App from './App';
import { createRoot } from 'react-dom/client';

createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ThemeProvider theme={THEME}>
      <Global styles={resetCss} />
      <App />
    </ThemeProvider>
  </React.StrictMode>,
);
