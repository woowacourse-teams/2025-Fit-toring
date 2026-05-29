import CommunityPostCard from '../CommunityPostCard/CommunityPostCard';

import type { CommunityPost } from '../../../../common/types/communityPost';

interface CommunityFeedProps {
  posts: CommunityPost[];
}

function CommunityFeed({ posts }: CommunityFeedProps) {
  return (
    <ul>
      {posts.map((post) => (
        <CommunityPostCard key={post.id} post={post} />
      ))}
    </ul>
  );
}

export default CommunityFeed;
