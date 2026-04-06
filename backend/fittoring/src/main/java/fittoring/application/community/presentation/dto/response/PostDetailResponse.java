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
        LocalDateTime createdAt
) {

    public static PostDetailResponse from(Post post) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getNickname(),
                post.isAnonymous(),
                post.isGuestPost(),
                post.getCreatedAt()
        );
    }
}
