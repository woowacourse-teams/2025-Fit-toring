package fittoring.application.community.presentation.dto.response;

import fittoring.domain.model.Post;
import java.time.LocalDateTime;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        String nickname,
        boolean isAnonymous,
        boolean isGuestPost,
        int commentCount,
        int viewCount,
        int likeCount,
        boolean liked,
        LocalDateTime createdAt
) {

    public static PostDetailResponse from(Post post, int commentCount, boolean liked) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getNickname(),
                post.isAnonymous(),
                post.isGuestPost(),
                commentCount,
                post.getViewCount(),
                post.getLikeCount(),
                liked,
                post.getCreatedAt()
        );
    }
}
