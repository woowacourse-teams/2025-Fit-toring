import styled from '@emotion/styled';

import Skeleton from '../../../../common/components/Skeleton/Skeleton';

function ChatRoomInfoSkeleton() {
  return (
    <S_ChatRoomInfoSkeleton>
      <S_HeaderSkeleton>
        <S_HeaderTitleSkeleton />
      </S_HeaderSkeleton>
      <S_ActionPanelSkeleton>
        <S_MentorInfoSkeleton>
          <S_SkeletonSquare />
          <S_MentorTextSkeleton>
            <S_MentorNameSkeleton />
            <S_MentorMetaSkeleton />
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

const S_HeaderTitleSkeleton = styled(Skeleton)`
  width: 45%;
  height: 1.6rem;
`;

const S_MentorNameSkeleton = styled(Skeleton)`
  width: 6rem;
  height: 1.6rem;
`;

const S_MentorMetaSkeleton = styled(Skeleton)`
  width: 4rem;
  height: 1.6rem;
`;

const S_SkeletonSquare = styled(Skeleton)`
  width: 4rem;
  height: 4rem;
  border-radius: 10px;
`;

const S_SkeletonButton = styled(Skeleton)`
  width: 6rem;
  height: 100%;
  padding: 0.7rem 1.3rem;
  border-radius: 0.5rem;
`;
