export interface LikeState {
  liked: boolean;
  likeCount: number;
}

export interface PostLikeResponse extends LikeState {
  postId: number;
}

export interface CommentLikeResponse extends LikeState {
  commentId: number;
}
