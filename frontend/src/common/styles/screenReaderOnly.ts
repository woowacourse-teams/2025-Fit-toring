import { css } from '@emotion/react';

export const screenReaderOnlyStyle = css`
  overflow: hidden;
  clip-path: inset(50%);
  position: absolute;

  width: 1px;
  height: 1px;
  margin: -1px;
  padding: 0;
  border: 0;

  white-space: nowrap;
`;
