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

export interface CommunityPostDetail extends CommunityPost {
  isGuestPost: boolean;
}
