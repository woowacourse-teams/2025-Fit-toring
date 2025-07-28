import { Global, ThemeProvider } from '@emotion/react';
import styled from '@emotion/styled';

import { fonts } from '../src/common/styles/fonts';
import { resetCss } from '../src/common/styles/reset';
import { THEME } from '../src/common/styles/theme';

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
  tags: ['autodocs'],

  decorators: [
    (Story) => {
      return (
        <ThemeProvider theme={THEME}>
          <Global styles={[resetCss, fonts]} />
          <StyledContainer>
            <StyledContents>
              <Story />
            </StyledContents>
          </StyledContainer>
        </ThemeProvider>
      );
    },
  ],
};

export default preview;

// MobileLayout 에서 배경 색상 제거한 StyledComponents
const StyledContainer = styled.main`
  display: flex;
  justify-content: center;

  width: 100%;
  height: fit-content;
  min-height: 100%;
`;

const StyledContents = styled.section`
  width: 48rem;

  @media screen and (width <= 480px) {
    width: 100%;
    border: none;
  }
`;
