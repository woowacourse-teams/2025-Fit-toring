import { css } from '@emotion/react';

export const resetCss = css`
  html,
  body,
  div,
  span,
  h1,
  h2,
  h3,
  h4,
  h5,
  p,
  pre,
  a,
  img,
  strong,
  ol,
  ul,
  li,
  fieldset,
  form,
  label,
  legend,
  table,
  caption,
  tbody,
  tfoot,
  thead,
  tr,
  th,
  td,
  article,
  aside,
  details,
  footer,
  header,
  nav,
  section,
  summary,
  audio,
  video {
    margin: 0;
    padding: 0;
    border: 0;

    font: inherit;

    font-size: 100%;
    vertical-align: baseline;
  }

  /* HTML5 display-role reset for older browsers */
  article,
  aside,
  details,
  footer,
  header,
  nav,
  section {
    display: block;
  }

  html,
  * {
    font-size: 62.5%;
    font-family: Pretendard, sans-serif;
  }

  body {
    line-height: 1;
  }

  html,
  body,
  #root {
    height: 100%;
  }

  ol,
  ul {
    list-style: none;
  }

  table {
    border-collapse: collapse;
    border-spacing: 0;
  }

  *,
  *::before,
  *::after {
    box-sizing: border-box;
  }
`;
