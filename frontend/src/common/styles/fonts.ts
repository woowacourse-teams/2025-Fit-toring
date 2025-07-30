import { css } from '@emotion/react';

import PretendardBold from '../assets/fonts/Pretendard-Bold.woff2';
import PretendardRegular from '../assets/fonts/Pretendard-Regular.woff2';

export const fonts = css`
  @font-face {
    font-family: Pretendard;
    font-weight: 400;
    src: url(${PretendardRegular}) format('woff2');
  }

  @font-face {
    font-family: Pretendard;
    font-weight: 700;
    src: url(${PretendardBold}) format('woff2');
  }
`;
