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

export interface CommunityPostResponse {
  posts: CommunityPost[];
  nextCursorCode: string | null;
  hasNext: boolean;
}
