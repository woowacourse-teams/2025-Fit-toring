import '@emotion/react';
import type { THEME } from '../styles/theme';
type myTheme = typeof THEME;

declare module '@emotion/react' {
  // eslint-disable-next-line @typescript-eslint/no-empty-object-type
  export interface Theme extends myTheme {}
}
