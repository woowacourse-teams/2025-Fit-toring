import styled from '@emotion/styled';

import Skeleton from '../../../../common/components/Skeleton/Skeleton';

function CommunityPostCardSkeleton() {
  return (
    <S_ListItem>
      <S_Card>
        <S_TitleSkeleton />
        <S_ContentSkeleton />
        <S_FooterRow>
          <S_FooterLeftSkeleton />
          <S_FooterRightSkeleton />
        </S_FooterRow>
      </S_Card>
    </S_ListItem>
  );
}

export default CommunityPostCardSkeleton;

const S_ListItem = styled.li`
  list-style: none;
`;

const S_Card = styled.article`
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  position: relative;

  min-height: 10.4rem;
  padding: 1.6rem;

  &::after {
    content: '';

    position: absolute;
    right: 1.6rem;
    bottom: 0;
    left: 1.6rem;

    height: 1px;

    background-color: ${({ theme }) => theme.OUTLINE.LIGHT};
  }
`;

const S_TitleSkeleton = styled(Skeleton)`
  width: 62%;
  height: 2.2rem;
`;

const S_ContentSkeleton = styled(Skeleton)`
  width: 86%;
  height: 1.8rem;
`;

const S_FooterRow = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.2rem;

  margin-top: 0.2rem;
`;

const S_FooterLeftSkeleton = styled(Skeleton)`
  width: 34%;
  height: 1.5rem;
`;

const S_FooterRightSkeleton = styled(Skeleton)`
  width: 14%;
  height: 1.5rem;
`;
