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
          <S_Indicator />
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
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 50%;
  box-shadow: 0 0.4rem 1.2rem rgb(15 23 42 / 12%);

  background-color: ${({ theme }) => theme.BG.WHITE};
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
  width: 1.8rem;
  height: 1.8rem;
  border: 3px solid ${({ theme }) => theme.SYSTEM.GRAY200};
  border-left-color: ${({ theme }) => theme.SYSTEM.MAIN500};

  border-radius: 50%;

  .${PULL_TO_REFRESH_REFRESHING_CLASS} & {
    animation: ${spinIndicator} 0.8s linear infinite;
  }

  @media (prefers-reduced-motion: reduce) {
    .${PULL_TO_REFRESH_REFRESHING_CLASS} & {
      animation-duration: 1.6s;
    }
  }
`;
