import styled from '@emotion/styled';

import CommunityPostCard from '../CommunityPostCard/CommunityPostCard';

export interface CommunityPost {
  id: number;
  title: string;
  nickname: string;
  isAnonymous: boolean;
  createdAt: string;
  commentCount: number;
  viewCount: number;
  likeCount: number;
  content: string;
}

interface CommunityFeedProps {
  posts: CommunityPost[];
}

function CommunityFeed({ posts }: CommunityFeedProps) {
  return (
    <S_List>
      {posts.map((post) => (
        <CommunityPostCard key={post.id} post={post} />
      ))}
    </S_List>
  );
}

export default CommunityFeed;

const S_List = styled.ul`
  border-top: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;
