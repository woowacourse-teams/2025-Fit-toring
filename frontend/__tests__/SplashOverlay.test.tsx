import { act, render } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import SplashOverlay from '../src/common/components/SplashOverlay/SplashOverlay';

const SPLASH_SELECTOR = 'img[src="/splash/fittoring-splash.png"]';

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

function mockUserAgent(userAgent: string) {
  Object.defineProperty(window.navigator, 'userAgent', {
    configurable: true,
    value: userAgent,
  });
}

describe('SplashOverlay', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    sessionStorage.clear();
    mockUserAgent(
      'Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/125.0.0.0 Mobile Safari/537.36',
    );
  });

  afterEach(() => {
    vi.useRealTimers();
    vi.restoreAllMocks();
    sessionStorage.clear();
  });

  it('iOS가 아닌 PWA standalone 실행이면 정적 splash 이미지를 보여준다', () => {
    mockMatchMedia({ standalone: true });

    const { container } = render(<SplashOverlay />);

    expect(container.querySelector(SPLASH_SELECTOR)).toBeInTheDocument();
  });

  it('iOS PWA standalone 실행이면 정적 splash 이미지를 보여주지 않는다', () => {
    mockUserAgent(
      'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 Version/17.0 Mobile/15E148 Safari/604.1',
    );
    mockMatchMedia({ standalone: true });

    const { container } = render(<SplashOverlay />);

    expect(container.querySelector(SPLASH_SELECTOR)).not.toBeInTheDocument();
  });

  it('PWA standalone 실행이 아니면 정적 splash 이미지를 보여주지 않는다', () => {
    mockMatchMedia({ standalone: false });

    const { container } = render(<SplashOverlay />);

    expect(container.querySelector(SPLASH_SELECTOR)).not.toBeInTheDocument();
  });

  it('사용자가 reduced motion을 선호해도 정적 splash 이미지를 보여준다', () => {
    mockMatchMedia({ standalone: true, reducedMotion: true });

    const { container } = render(<SplashOverlay />);

    expect(container.querySelector(SPLASH_SELECTOR)).toBeInTheDocument();
  });

  it('한 세션에서 정적 splash 이미지를 한 번만 보여준다', () => {
    mockMatchMedia({ standalone: true });

    const { container, unmount } = render(<SplashOverlay />);

    expect(container.querySelector(SPLASH_SELECTOR)).toBeInTheDocument();

    act(() => {
      vi.advanceTimersByTime(1180);
    });

    expect(container.querySelector(SPLASH_SELECTOR)).not.toBeInTheDocument();

    unmount();
    const { container: nextContainer } = render(<SplashOverlay />);

    expect(nextContainer.querySelector(SPLASH_SELECTOR)).not.toBeInTheDocument();
  });
});
