import styled from '@emotion/styled';
import { useParams } from 'react-router-dom';

import CommunityPostDetailHeader from './components/CommunityPostDetailHeader/CommunityPostDetailHeader';
import InputSection from './components/InputSection/InputSection';
import PostCommentSection from './components/PostCommentSection/PostCommentSection';
import PostContent from './components/PostContent/PostContent';
import PostHeader from './components/PostHeader/PostHeader';

function CommunityPostDetail() {
  const { postId } = useParams();

  const post = {
    id: 13,
    title: '주말 오전 같이 러닝하실 분 있나요?',
    nickname: '작성자E',
    profileImageUrl: null,
    isAnonymous: false,
    createdAt: '2026-04-02T09:10:00',
    commentCount: 2,
    viewCount: 87,
    likeCount: 5,
    content:
      '토요일 아침에 가볍게 5km 정도 뛰고 브런치 먹을 분 구합니다. 초보도 괜찮아요.',
  };

  return (
    <S_Container>
      <CommunityPostDetailHeader />
      <S_Content>
        <PostHeader
          viewCount={post.viewCount}
          createdAt={post.createdAt}
          nickname={post.nickname}
          profileImageUrl={post.profileImageUrl}
        />
        <PostContent title={post.title} content={post.content} />
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

  min-height: 100vh;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Content = styled.div`
  display: flex;
  flex: 1;
  flex-direction: column;
`;
