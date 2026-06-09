import styled from '@emotion/styled';

import LoadingSpinner from '../../../../common/components/LoadingSpinner/LoadingSpinner';
import Skeleton from '../../../../common/components/Skeleton/Skeleton';
import { BOTTOM_NAV_HEIGHT } from '../../../../common/constants/layout';
import CommunityPostDetailHeader from '../CommunityPostDetailHeader/CommunityPostDetailHeader';

function CommunityPostDetailSkeleton() {
  const noop = () => {};

  return (
    <S_Container>
      <CommunityPostDetailHeader
        showActionButton={false}
        onEditClick={noop}
        onDeleteClick={noop}
      />
      <S_Content>
        <S_ScreenReaderOnly role="status">
          게시글을 불러오는 중입니다.
        </S_ScreenReaderOnly>
        <S_PostHeaderSkeleton>
          <S_ProfileImageSkeleton />
          <S_HeaderTextGroup>
            <S_NicknameSkeleton />
            <S_MetaSkeleton />
          </S_HeaderTextGroup>
        </S_PostHeaderSkeleton>
        <S_PostContentSkeleton>
          <S_TitleSkeleton />
          <S_ContentLineSkeleton />
          <S_ContentLineSkeleton />
          <S_ContentShortLineSkeleton />
          <S_LikeSkeleton />
        </S_PostContentSkeleton>
        <S_CommentSectionSkeleton>
          {/* <S_CommentTitleSkeleton /> */}
          <S_Title>댓글</S_Title>
          <S_CommentSpinnerWrapper>
            <LoadingSpinner />
          </S_CommentSpinnerWrapper>
        </S_CommentSectionSkeleton>
      </S_Content>
      <S_InputSkeleton>
        <S_InputBarSkeleton />
        <S_SubmitSkeleton />
      </S_InputSkeleton>
    </S_Container>
  );
}

export default CommunityPostDetailSkeleton;

const S_Container = styled.main`
  display: flex;
  flex-direction: column;

  height: calc(100dvh - ${BOTTOM_NAV_HEIGHT}rem);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Content = styled.div`
  display: flex;
  flex: 1;
  flex-direction: column;

  min-height: 0;
  overflow-y: auto;
`;

const S_PostHeaderSkeleton = styled.section`
  display: flex;
  align-items: center;
  gap: 1.2rem;

  padding: 2rem 2rem 1.6rem;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_HeaderTextGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
`;

const S_PostContentSkeleton = styled.section`
  display: flex;
  flex-direction: column;
  gap: 1.6rem;

  padding: 2.4rem 2rem 3.2rem;
  border-bottom: 0.8rem solid ${({ theme }) => theme.SYSTEM.GRAY50};
`;

const S_CommentSectionSkeleton = styled.section`
  display: flex;
  flex: 1;
  flex-direction: column;

  padding: 2.4rem 2rem 0;
`;

const S_Title = styled.h3`
  padding-bottom: 1.6rem;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_SB}
`;

const S_CommentSpinnerWrapper = styled.div`
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;

  min-height: 16rem;
  border-top: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_InputSkeleton = styled.div`
  display: flex;
  flex-shrink: 0;
  align-items: center;
  gap: 1rem;

  padding: 1rem 1.6rem 1.2rem;
  border-top: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  box-shadow: 0 -0.4rem 1.6rem rgb(0 0 0 / 5%);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_ProfileImageSkeleton = styled(Skeleton)`
  flex-shrink: 0;

  width: 5rem;
  height: 5rem;
  border-radius: 50%;
`;

const S_NicknameSkeleton = styled(Skeleton)`
  width: 7.2rem;
  height: 2rem;
`;

const S_MetaSkeleton = styled(Skeleton)`
  width: 11rem;
  height: 1.5rem;
`;

const S_TitleSkeleton = styled(Skeleton)`
  width: 72%;
  height: 2.3rem;
`;

const S_ContentLineSkeleton = styled(Skeleton)`
  width: 100%;
  height: 1.8rem;
`;

const S_ContentShortLineSkeleton = styled(Skeleton)`
  width: 64%;
  height: 1.8rem;
`;

const S_LikeSkeleton = styled(Skeleton)`
  width: 7.2rem;
  height: 3rem;
`;

const S_InputBarSkeleton = styled(Skeleton)`
  flex: 1;

  height: 4.8rem;
  border-radius: 999px;
`;

const S_SubmitSkeleton = styled(Skeleton)`
  flex-shrink: 0;

  width: 3.2rem;
  height: 3.2rem;
  border-radius: 50%;
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
