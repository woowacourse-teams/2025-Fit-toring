import type { CommunityPost } from '../../../common/types/communityPost';

export interface CommunityPostResponse {
  posts: CommunityPost[];
  nextCursorCode: string | null;
  hasNext: boolean;
}
