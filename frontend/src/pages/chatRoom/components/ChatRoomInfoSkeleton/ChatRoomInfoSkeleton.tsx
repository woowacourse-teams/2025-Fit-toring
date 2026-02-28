import { keyframes } from '@emotion/react';
import styled from '@emotion/styled';

function ChatRoomInfoSkeleton() {
  return (
    <S_ChatRoomInfoSkeleton>
      <S_HeaderSkeleton>
        <S_SkeletonBar width="45%" />
      </S_HeaderSkeleton>
      <S_ActionPanelSkeleton>
        <S_MentorInfoSkeleton>
          <S_SkeletonSquare />
          <S_MentorTextSkeleton>
            <S_SkeletonBar width="6rem" />
            <S_SkeletonBar width="4rem" />
          </S_MentorTextSkeleton>
        </S_MentorInfoSkeleton>
        <S_ButtonSkeletonRow>
          <S_SkeletonButton />
          <S_SkeletonButton />
        </S_ButtonSkeletonRow>
      </S_ActionPanelSkeleton>
    </S_ChatRoomInfoSkeleton>
  );
}

export default ChatRoomInfoSkeleton;

const skeletonShimmer = keyframes`
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
`;

const S_ChatRoomInfoSkeleton = styled.div`
  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_HeaderSkeleton = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  position: relative;

  width: 100%;
  height: 5.7rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_ActionPanelSkeleton = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: stretch;
  justify-content: space-between;
  gap: 1.2rem;

  padding: 1.2rem;
  padding-top: 0;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_MentorInfoSkeleton = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const S_MentorTextSkeleton = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
`;

const S_ButtonSkeletonRow = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const S_SkeletonBlock = styled.div`
  border-radius: 6px;

  background: linear-gradient(
    90deg,
    ${({ theme }) => theme.SYSTEM.GRAY100} 0%,
    ${({ theme }) => theme.SYSTEM.GRAY50} 50%,
    ${({ theme }) => theme.SYSTEM.GRAY100} 100%
  );

  animation: ${skeletonShimmer} 1.2s ease-in-out infinite;
  background-size: 400% 100%;
`;

const S_SkeletonBar = styled(S_SkeletonBlock)<{ width: string }>`
  width: ${({ width }) => width};
  height: 1.6rem;
`;

const S_SkeletonSquare = styled(S_SkeletonBlock)`
  width: 4rem;
  height: 4rem;
  border-radius: 10px;
`;

const S_SkeletonButton = styled(S_SkeletonBlock)`
  width: 6rem;
  height: 100%;
  padding: 0.7rem 1.3rem;
  border-radius: 0.5rem;
`;
