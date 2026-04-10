export interface PostComment {
  id: number;
  nickname: string;
  content: string;
  isAnonymous: boolean;
  isGuestComment: boolean;
  rootId: number | null;
  parentId: number | null;
  isDeleted: boolean;
  createdAt: string;
}
