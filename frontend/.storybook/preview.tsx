import { Global } from '@emotion/react';

import { resetCss } from '../src/common/styles/reset';

import type { Preview } from '@storybook/react-webpack5';

const preview: Preview = {
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
  },
  decorators: [
    (Story) => {
      return (
        <>
          <Global styles={resetCss} />
          <Story />
        </>
      );
    },
  ],
};

export default preview;
