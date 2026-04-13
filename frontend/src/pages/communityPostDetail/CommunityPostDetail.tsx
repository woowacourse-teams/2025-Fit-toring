import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import LoadingSpinner from '../../common/components/LoadingSpinner/LoadingSpinner';

import { getCommunityPostDetail } from './apis/getCommunityPostDetail';
import CommunityPostDetailHeader from './components/CommunityPostDetailHeader/CommunityPostDetailHeader';
import InputSection from './components/InputSection/InputSection';
import PostCommentSection from './components/PostCommentSection/PostCommentSection';
import PostContent from './components/PostContent/PostContent';
import PostHeader from './components/PostHeader/PostHeader';

function CommunityPostDetail() {
  const { postId } = useParams();
  const { authenticated } = useAuth();
  const {
    data: postData,
    isPending,
    isError,
  } = useQuery({
    queryKey: ['communityPostDetail', postId],
    queryFn: () => getCommunityPostDetail(postId!),
    enabled: Boolean(postId),
  });

  if (isPending) {
    return <LoadingSpinner />;
  }

  if (isError || !postData) {
    return <div>게시글을 불러오지 못했습니다.</div>;
  }

  const canManagePost =
    postData.isGuestPost || (authenticated && postData.isMine);

  return (
    <S_Container>
      <CommunityPostDetailHeader
        showActionButton={canManagePost}
        onEditClick={() => {}}
        onDeleteClick={() => {}}
      />
      <S_Content>
        <PostHeader
          createdAt={postData.createdAt}
          nickname={postData.isAnonymous ? '익명' : postData.nickname}
          viewCount={postData.viewCount}
        />
        <PostContent
          title={postData.title}
          content={postData.content}
          likeCount={postData.likeCount}
        />
        <PostCommentSection postId={postId ?? ''} />
      </S_Content>
      <InputSection />
    </S_Container>
  );
}

export default CommunityPostDetail;

const S_Container = styled.main`
  display: flex;
  flex-direction: column;

  min-height: 100dvh;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Content = styled.div`
  display: flex;
  flex: 1;
  flex-direction: column;

  padding-bottom: 7rem;
`;
