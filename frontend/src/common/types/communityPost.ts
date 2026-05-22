import type { LikeState } from './like';

export interface CommunityPost extends LikeState {
  id: number;
  title: string;
  nickname: string;
  isAnonymous: boolean;
  createdAt: string;
  commentCount: number;
  viewCount: number;
  content: string;
}

export interface CommunityPostDetail extends CommunityPost {
  isGuestPost: boolean;
}
