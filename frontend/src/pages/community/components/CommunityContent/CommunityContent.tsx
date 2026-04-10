import { useCallback } from 'react';

import styled from '@emotion/styled';

import useInfiniteScroll from '../../../../common/hooks/useInfiniteScroll';
import useInfiniteCommunityPosts from '../../hooks/useInfiniteCommunityPosts';
import CommunityFeed from '../CommunityFeed/CommunityFeed';

function CommunityContent() {
  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isPending } =
    useInfiniteCommunityPosts();

  const communityPosts =
    data?.pages.flatMap((page) => page.posts) ?? [];

  const handleIntersect = useCallback(async () => {
    await fetchNextPage();
  }, [fetchNextPage]);

  const { targetRef } = useInfiniteScroll<HTMLDivElement>({
    isReady: !!hasNextPage && !isFetchingNextPage,
    onIntersect: handleIntersect,
  });

  return (
    <S_Container>
      {!isPending && <CommunityFeed posts={communityPosts} />}
      <S_ObserverTarget ref={targetRef} />
      {isPending && <S_StatusText>게시글을 불러오는 중입니다.</S_StatusText>}
      {isFetchingNextPage && (
        <S_StatusText>게시글을 더 불러오는 중입니다.</S_StatusText>
      )}
    </S_Container>
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
