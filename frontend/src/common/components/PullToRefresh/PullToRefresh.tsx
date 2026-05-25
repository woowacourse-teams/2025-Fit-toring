import { useRef, type ReactNode } from 'react';

import styled from '@emotion/styled';

import usePullToRefresh from './usePullToRefresh';

interface PullToRefreshProps {
  enabled: boolean;
  onRefresh: () => Promise<void> | void;
  children: ReactNode;
}

function PullToRefresh({ enabled, onRefresh, children }: PullToRefreshProps) {
  const rootRef = useRef<HTMLDivElement | null>(null);
  const indicatorRef = useRef<HTMLDivElement | null>(null);

  usePullToRefresh({
    enabled,
    onRefresh,
    rootRef,
    indicatorRef,
  });

  return (
    <S_Container ref={rootRef}>
      <S_IndicatorWrapper aria-hidden="true" ref={indicatorRef}>
        <S_Indicator />
      </S_IndicatorWrapper>
      {children}
    </S_Container>
  );
}

export default PullToRefresh;

const S_Container = styled.div`
  position: relative;
`;

const S_IndicatorWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  position: fixed;
  top: 0;
  left: 50%;
  z-index: 200;

  width: 4rem;
  height: 4rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 50%;
  box-shadow: 0 0.4rem 1.2rem rgb(15 23 42 / 12%);

  background-color: ${({ theme }) => theme.BG.WHITE};
  pointer-events: none;
  opacity: 0;

  transform: translate3d(-50%, -4.8rem, 0) rotate(0deg);
  will-change: transform, opacity;
`;

const S_Indicator = styled.div`
  width: 1.8rem;
  height: 1.8rem;
  border: 3px solid ${({ theme }) => theme.SYSTEM.GRAY200};
  border-left-color: ${({ theme }) => theme.SYSTEM.MAIN500};

  border-radius: 50%;
`;
