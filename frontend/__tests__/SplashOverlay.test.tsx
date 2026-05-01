import { act, render } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import SplashOverlay from '../src/common/components/SplashOverlay/SplashOverlay';

const SPLASH_SELECTOR = 'img[src="/splash/fittoring-splash.gif"]';

function mockMatchMedia({
  standalone,
  reducedMotion = false,
}: {
  standalone: boolean;
  reducedMotion?: boolean;
}) {
  Object.defineProperty(window, 'matchMedia', {
    writable: true,
    value: vi.fn().mockImplementation((query: string) => {
      return {
        matches:
          (query === '(display-mode: standalone)' && standalone) ||
          (query === '(prefers-reduced-motion: reduce)' && reducedMotion),
        media: query,
        onchange: null,
        addListener: vi.fn(),
        removeListener: vi.fn(),
        addEventListener: vi.fn(),
        removeEventListener: vi.fn(),
        dispatchEvent: vi.fn(),
      } as MediaQueryList;
    }),
  });
}

describe('SplashOverlay', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    sessionStorage.clear();
  });

  afterEach(() => {
    vi.useRealTimers();
    vi.restoreAllMocks();
    sessionStorage.clear();
  });

  it('PWA standalone 실행이면 GIF splash를 보여준다', () => {
    mockMatchMedia({ standalone: true });

    const { container } = render(<SplashOverlay />);

    expect(container.querySelector(SPLASH_SELECTOR)).toBeInTheDocument();
  });

  it('PWA standalone 실행이 아니면 GIF splash를 보여주지 않는다', () => {
    mockMatchMedia({ standalone: false });

    const { container } = render(<SplashOverlay />);

    expect(container.querySelector(SPLASH_SELECTOR)).not.toBeInTheDocument();
  });

  it('사용자가 reduced motion을 선호하면 GIF splash를 보여주지 않는다', () => {
    mockMatchMedia({ standalone: true, reducedMotion: true });

    const { container } = render(<SplashOverlay />);

    expect(container.querySelector(SPLASH_SELECTOR)).not.toBeInTheDocument();
  });

  it('한 세션에서 GIF splash를 한 번만 보여준다', () => {
    mockMatchMedia({ standalone: true });

    const { container, unmount } = render(<SplashOverlay />);

    expect(container.querySelector(SPLASH_SELECTOR)).toBeInTheDocument();

    act(() => {
      vi.advanceTimersByTime(3320);
    });

    expect(container.querySelector(SPLASH_SELECTOR)).not.toBeInTheDocument();

    unmount();
    const { container: nextContainer } = render(<SplashOverlay />);

    expect(nextContainer.querySelector(SPLASH_SELECTOR)).not.toBeInTheDocument();
  });
});
