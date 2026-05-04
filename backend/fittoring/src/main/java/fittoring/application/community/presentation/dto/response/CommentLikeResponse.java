package fittoring.application.community.presentation.dto.response;

public record CommentLikeResponse(
        Long commentId,
        boolean liked,
        int likeCount
) {

    public static CommentLikeResponse ofUnlike(Long commentId, int likeCount) {
        return new CommentLikeResponse(commentId, false, likeCount);
    }

    public static CommentLikeResponse ofLike(Long commentId, int likeCount) {
        return new CommentLikeResponse(commentId, true, likeCount);
    }
}
