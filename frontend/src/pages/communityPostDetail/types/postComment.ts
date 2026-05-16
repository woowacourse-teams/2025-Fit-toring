export interface PostCommentResponse {
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

export interface PostComment extends PostCommentResponse {
  isMine: boolean;
}

export interface PostCommentRequest {
  content: string;
  isAnonymous?: boolean;
  nickname?: string;
  guestPassword?: string;
  rootId: number | null;
  parentId: number | null;
}

export interface PatchPostCommentRequest {
  content: string;
  guestPassword?: string;
}

export interface DeletePostCommentRequest {
  guestPassword?: string;
}
