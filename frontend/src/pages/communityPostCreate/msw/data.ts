import type { CommunityPostDetail } from '../../../common/types/communityPost';

export const CREATED_COMMUNITY_POST: CommunityPostDetail = {
  id: 1,
  title: '게시글 제목',
  content: '게시글 본문',
  nickname: '작성자명',
  isAnonymous: false,
  isGuestPost: true,
  createdAt: '2026-04-06T21:30:00',
  viewCount: 0,
  likeCount: 0,
  commentCount: 0,
  liked: false,
} as const;
