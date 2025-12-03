import type { ReactElement } from 'react';

import { ThemeProvider } from '@emotion/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { THEME } from '../src/common/styles/theme';

import type { RenderOptions } from '@testing-library/react';
import type { MemoryRouterProps } from 'react-router-dom';

const createTestQueryClient = () => {
  return new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
      mutations: {
        retry: false,
      },
    },
  });
};

interface CustomRenderOptions extends Omit<RenderOptions, 'wrapper'> {
  routerProps?: MemoryRouterProps;
}

const customRender = (
  ui: ReactElement,
  {
    routerProps = { initialEntries: ['/'] },
    ...renderOptions
  }: CustomRenderOptions = {},
) => {
  const queryClient = createTestQueryClient();

  const Wrapper = ({ children }: { children: React.ReactNode }) => {
    return (
      <QueryClientProvider client={queryClient}>
        <ThemeProvider theme={THEME}>
          <MemoryRouter {...routerProps}>{children}</MemoryRouter>
        </ThemeProvider>
      </QueryClientProvider>
    );
  };

  return render(ui, { wrapper: Wrapper, ...renderOptions });
};

export { customRender as render };
