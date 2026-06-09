import styled from '@emotion/styled';

import Skeleton from '../../../../common/components/Skeleton/Skeleton';

function MentorInfoCardSkeleton() {
  return (
    <S_Container aria-hidden="true">
      <S_ProfileWrapper>
        <S_ProfileImageSkeleton />
        <S_NameSkeleton />
      </S_ProfileWrapper>
      <S_InfoWrapper>
        <S_InfoLineSkeleton />
        <S_ShortInfoLineSkeleton />
      </S_InfoWrapper>
      <S_PriceSkeleton />
    </S_Container>
  );
}

export default MentorInfoCardSkeleton;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.4rem;

  width: 100%;
  padding: 2.2rem;
  border: ${({ theme }) => theme.OUTLINE.REGULAR} 0.1rem solid;
  border-radius: 1.27rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_ProfileWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.4rem;
`;

const S_ProfileImageSkeleton = styled(Skeleton)`
  width: 6.4rem;
  height: 6.4rem;
  border-radius: 50%;
`;

const S_NameSkeleton = styled(Skeleton)`
  width: 8rem;
  height: 2.2rem;
`;

const S_InfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.7rem;
`;

const S_InfoLineSkeleton = styled(Skeleton)`
  width: 16rem;
  height: 1.8rem;
`;

const S_ShortInfoLineSkeleton = styled(Skeleton)`
  width: 12rem;
  height: 1.8rem;
`;

const S_PriceSkeleton = styled(Skeleton)`
  width: 8.6rem;
  height: 2.4rem;
`;
