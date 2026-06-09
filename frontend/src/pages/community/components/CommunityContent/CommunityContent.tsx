import { useCallback } from 'react';

import styled from '@emotion/styled';

import PullToRefresh from '../../../../common/components/PullToRefresh/PullToRefresh';
import { isPullToRefreshEnabled } from '../../../../common/components/PullToRefresh/utils';
import useInfiniteScroll from '../../../../common/hooks/useInfiniteScroll';
import useInfiniteCommunityPosts from '../../hooks/useInfiniteCommunityPosts';
import CommunityFeed from '../CommunityFeed/CommunityFeed';
import CommunityPostCardSkeleton from '../CommunityPostCard/CommunityPostCardSkeleton';

const COMMUNITY_POST_SKELETON_COUNT = 8;

function CommunityContent() {
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
      <S_Container>
        {isPending ? (
          <>
            <S_ScreenReaderOnly role="status">
              게시글을 불러오는 중입니다.
            </S_ScreenReaderOnly>
            <S_SkeletonList role="presentation">
              {Array.from({ length: COMMUNITY_POST_SKELETON_COUNT }).map(
                (_, index) => (
                  <CommunityPostCardSkeleton key={index} />
                ),
              )}
            </S_SkeletonList>
          </>
        ) : (
          <CommunityFeed posts={communityPosts} />
        )}
        <S_ObserverTarget ref={targetRef} />
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

const S_SkeletonList = styled.ul`
  min-height: 100%;
`;

const S_ScreenReaderOnly = styled.p`
  overflow: hidden;
  position: absolute;

  width: 1px;
  height: 1px;
  margin: -1px;
  padding: 0;
  border: 0;

  white-space: nowrap;
  clip: rect(0, 0, 0, 0);
`;

const S_StatusText = styled.p`
  padding: 1.6rem;

  color: ${({ theme }) => theme.SYSTEM.GRAY600};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  text-align: center;
`;
