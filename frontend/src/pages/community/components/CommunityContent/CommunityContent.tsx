import { useCallback, useLayoutEffect, useRef } from 'react';

import styled from '@emotion/styled';

import PullToRefresh from '../../../../common/components/PullToRefresh/PullToRefresh';
import { isPullToRefreshEnabled } from '../../../../common/components/PullToRefresh/utils';
import useInfiniteScroll from '../../../../common/hooks/useInfiniteScroll';
import useInfiniteCommunityPosts from '../../hooks/useInfiniteCommunityPosts';
import {
  clearCommunityScrollY,
  getMaxCommunityScrollY,
  getCommunityScrollY,
  restoreCommunityScrollY,
} from '../../utils/communityScrollStorage';
import CommunityFeed from '../CommunityFeed/CommunityFeed';

function CommunityContent() {
  const containerRef = useRef<HTMLElement | null>(null);
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
    refetch,
  } = useInfiniteCommunityPosts();

  const communityPosts =
    data?.pages.flatMap((page) => page.posts) ?? [];

  useLayoutEffect(() => {
    if (isPending) {
      return;
    }

    const savedScrollY = getCommunityScrollY();

    if (savedScrollY === null) {
      return;
    }

    const frameId = window.requestAnimationFrame(() => {
      const maxScrollY = getMaxCommunityScrollY(containerRef.current);

      if (savedScrollY > maxScrollY && hasNextPage) {
        if (isFetchingNextPage) {
          return;
        }

        void fetchNextPage();
        return;
      }

      restoreCommunityScrollY(
        Math.min(savedScrollY, maxScrollY),
        containerRef.current,
      );
      clearCommunityScrollY();
    });

    return () => window.cancelAnimationFrame(frameId);
  }, [
    communityPosts.length,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
  ]);

  const handleIntersect = useCallback(async () => {
    await fetchNextPage();
  }, [fetchNextPage]);

  const { targetRef } = useInfiniteScroll<HTMLDivElement>({
    isReady: !!hasNextPage && !isFetchingNextPage,
    onIntersect: handleIntersect,
  });

  const handleRefresh = async () => {
    await refetch();
  };

  return (
    <PullToRefresh
      enabled={isPullToRefreshEnabled()}
      onRefresh={handleRefresh}
    >
      <S_Container ref={containerRef}>
        {!isPending && <CommunityFeed posts={communityPosts} />}
        <S_ObserverTarget ref={targetRef} />
        {isPending && <S_StatusText>게시글을 불러오는 중입니다.</S_StatusText>}
        {isFetchingNextPage && (
          <S_StatusText>게시글을 더 불러오는 중입니다.</S_StatusText>
        )}
      </S_Container>
    </PullToRefresh>
  );
}

export default CommunityContent;

const S_Container = styled.main`
  position: relative;

  min-height: 100%;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_ObserverTarget = styled.div`
  width: 100%;
  height: 1px;
`;

const S_StatusText = styled.p`
  padding: 1.6rem;

  color: ${({ theme }) => theme.SYSTEM.GRAY600};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  text-align: center;
`;
