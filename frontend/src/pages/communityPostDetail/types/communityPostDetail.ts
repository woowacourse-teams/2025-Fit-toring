export interface CommunityPostDetail {
  id: number;
  title: string;
  content: string;
  nickname: string;
  isAnonymous: boolean;
  isGuestPost: boolean;
  createdAt: string;
  viewCount: number;
  likeCount: number;
  commentCount: number;
}
