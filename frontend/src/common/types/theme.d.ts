import '@emotion/react';

declare module '@emotion/react' {
  export interface Theme {
    SYSTEM: {
      GRAY900: '#111111';
      GRAY800: '#333333';
      GRAY700: '#505050';
      GRAY600: '#666666';
      GRAY500: '#767676';
      GRAY400: '#888888';
      GRAY300: '#999999';
      GRAY200: '#BBBBBB';
      GRAY100: '#E1E1E1';
      GRAY50: '#F1F1F5';
      MAIN900: '#004F45';
      MAIN800: '#006559';
      MAIN700: '#008777';
      MAIN600: '#00A995';
      MAIN500: '#00B49E';
      MAIN400: '#00CBB2';
      MAIN300: '#00E1C6';
      MAIN200: '#D9FBF6';
      MAIN100: '#B0F6ED';
      MAIN50: '#E6FCF9';
    };

    FONT: {
      B01: '#1D293D';
      B02: '#45556C';
      B03: '#62748E';
      B04: '#F1F5F9';
      W01: '#FFFFFF';
    };

    LINE: {
      LIGHT: '#F1F1F5';
      REGULAR: '#E2E8F0';
      BLACK: '#111111';
    };

    BG: {
      LIGHT: '#F8FAFC';
      BLACK: '#111111';
    };
  }
}
