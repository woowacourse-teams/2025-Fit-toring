package fittoring.application.community.presentation.dto.response;

public record PostLikeResponse(
        Long postId,
        boolean liked,
        int likeCount
) {

    public static PostLikeResponse ofUnlike(Long postId, int likeCount) {
        return new PostLikeResponse(postId, false, likeCount);
    }

    public static PostLikeResponse ofLike(Long postId, int likeCount) {
        return new PostLikeResponse(postId, true, likeCount);
    }
}
