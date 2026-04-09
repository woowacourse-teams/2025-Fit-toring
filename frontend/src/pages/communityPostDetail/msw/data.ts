import type { CommunityPostDetail } from '../types/communityPostDetail';

export const COMMUNITY_POST_DETAIL: CommunityPostDetail = {
  id: 1,
  title: '게시글 제목',
  content: '게시글 본문',
  nickname: '작성자명',
  isAnonymous: false,
  isGuestPost: false,
  createdAt: '2026-04-06T21:30:00',
} as const;
