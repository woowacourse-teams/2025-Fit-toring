import { useRef, type ReactNode } from 'react';

import { keyframes } from '@emotion/react';
import styled from '@emotion/styled';

import usePullToRefresh, {
  PULL_TO_REFRESH_REFRESHING_CLASS,
} from './usePullToRefresh';

interface PullToRefreshProps {
  enabled: boolean;
  onRefresh: () => Promise<void> | void;
  children: ReactNode;
}

function PullToRefresh({ enabled, onRefresh, children }: PullToRefreshProps) {
  const rootRef = useRef<HTMLDivElement | null>(null);
  const indicatorRef = useRef<HTMLDivElement | null>(null);
  const contentRef = useRef<HTMLDivElement | null>(null);

  usePullToRefresh({
    enabled,
    onRefresh,
    rootRef,
    indicatorRef,
    contentRef,
  });

  return (
    <S_Container ref={rootRef}>
      <S_IndicatorSlot aria-hidden="true">
        <S_IndicatorWrapper ref={indicatorRef}>
          <S_Indicator>
            {Array.from({ length: 8 }, (_, index) => (
              <span key={index} />
            ))}
          </S_Indicator>
        </S_IndicatorWrapper>
      </S_IndicatorSlot>
      <S_ContentMover ref={contentRef}>{children}</S_ContentMover>
    </S_Container>
  );
}

export default PullToRefresh;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  position: relative;

  width: 100%;
  min-height: inherit;

  overscroll-behavior-y: contain;
  touch-action: pan-y;
`;

const S_IndicatorSlot = styled.div`
  display: flex;
  justify-content: center;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 0;

  width: 100%;
  height: 7rem;

  pointer-events: none;
`;

const S_ContentMover = styled.div`
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  position: relative;
  z-index: 1;

  min-height: inherit;

  background-color: inherit;
  transform: translate3d(0, 0, 0);
`;

const S_IndicatorWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 4rem;
  height: 4rem;
  margin-top: 1.5rem;

  pointer-events: none;
  opacity: 0;

  transform: rotate(var(--pull-to-refresh-rotation, 0deg));
`;

const spinIndicator = keyframes`
  100% {
    transform: rotate(360deg);
  }
`;

const S_Indicator = styled.div`
  position: relative;

  width: 2.4rem;
  height: 2.4rem;

  .${PULL_TO_REFRESH_REFRESHING_CLASS} & {
    animation: ${spinIndicator} 0.8s linear infinite;
  }

  @media (prefers-reduced-motion: reduce) {
    .${PULL_TO_REFRESH_REFRESHING_CLASS} & {
      animation-duration: 1.6s;
    }
  }

  span {
    position: absolute;
    top: 0;
    left: 50%;

    width: 0.35rem;
    height: 0.9rem;
    border-radius: 999px;

    background-color: ${({ theme }) => theme.SYSTEM.GRAY500};
    transform-origin: center 1.2rem;
  }

  span:nth-of-type(1) {
    opacity: 1;
    transform: translateX(-50%) rotate(0deg);
  }

  span:nth-of-type(2) {
    opacity: 0.85;
    transform: translateX(-50%) rotate(45deg);
  }

  span:nth-of-type(3) {
    opacity: 0.75;
    transform: translateX(-50%) rotate(90deg);
  }

  span:nth-of-type(4) {
    opacity: 0.65;
    transform: translateX(-50%) rotate(135deg);
  }

  span:nth-of-type(5) {
    opacity: 0.55;
    transform: translateX(-50%) rotate(180deg);
  }

  span:nth-of-type(6) {
    opacity: 0.45;
    transform: translateX(-50%) rotate(225deg);
  }

  span:nth-of-type(7) {
    opacity: 0.35;
    transform: translateX(-50%) rotate(270deg);
  }

  span:nth-of-type(8) {
    opacity: 0.25;
    transform: translateX(-50%) rotate(315deg);
  }
`;
