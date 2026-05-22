import { keyframes } from '@emotion/react';
import styled from '@emotion/styled';

const SKELETON_COUNT = 3;

function MentorCardListSkeleton() {
  return (
    <>
      {Array.from({ length: SKELETON_COUNT }).map((_, index) => (
        <S_Container key={index} aria-hidden="true">
          <S_ImageBox>
            <S_ProfileImgSkeleton />
          </S_ImageBox>
          <S_Wrapper>
            <S_InfoWrapper>
              <S_TitleSkeleton />
              <S_SubtitleSkeleton />
            </S_InfoWrapper>
            <S_SelfIntroductionSkeleton>
              <S_Bar />
              <S_Bar />
            </S_SelfIntroductionSkeleton>
            <S_PriceWrapper>
              <S_TimeSkeleton />
              <S_PriceSkeleton />
            </S_PriceWrapper>
          </S_Wrapper>
        </S_Container>
      ))}
    </>
  );
}

export default MentorCardListSkeleton;

const skeletonShimmer = keyframes`
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
`;

const S_Container = styled.li`
  display: flex;

  width: 100%;
  height: 21.5rem;
  overflow: hidden;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 8px;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 0.8rem;

  width: 100%;
  height: 100%;
  padding: 1.4rem;
`;

const S_ImageBox = styled.div`
  flex-shrink: 0;
  overflow: hidden;

  width: 43%;
  height: 100%;
`;

const S_InfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
`;

const S_SelfIntroductionSkeleton = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
`;

const S_PriceWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.3rem;
`;

const S_SkeletonBlock = styled.div`
  border-radius: 6px;

  background: linear-gradient(
    90deg,
    ${({ theme }) => theme.SYSTEM.GRAY100} 0%,
    ${({ theme }) => theme.SYSTEM.GRAY50} 50%,
    ${({ theme }) => theme.SYSTEM.GRAY100} 100%
  );
  background-size: 400% 100%;

  animation: ${skeletonShimmer} 1.2s ease-in-out infinite;
`;

const S_Bar = styled(S_SkeletonBlock)`
  width: 100%;
  height: 1.2rem;
`;

const S_TitleSkeleton = styled(S_SkeletonBlock)`
  width: 60%;
  height: 2rem;
`;

const S_SubtitleSkeleton = styled(S_SkeletonBlock)`
  width: 42%;
  height: 1.4rem;
`;

const S_ProfileImgSkeleton = styled(S_SkeletonBlock)`
  width: 100%;
  height: 100%;
`;

const S_TimeSkeleton = styled(S_SkeletonBlock)`
  width: 3.4rem;
  height: 1.6rem;
`;

const S_PriceSkeleton = styled(S_SkeletonBlock)`
  width: 5.2rem;
  height: 1.8rem;
`;
