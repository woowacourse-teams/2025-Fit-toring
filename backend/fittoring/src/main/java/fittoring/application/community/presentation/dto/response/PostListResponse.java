package fittoring.application.community.presentation.dto.response;

import fittoring.domain.model.Post;
import java.time.LocalDateTime;
import java.util.List;

public record PostListResponse(
        List<PostSummary> posts,
        String nextCursorCode,
        boolean hasNext
) {

    public record PostSummary(
            Long id,
            String title,
            String nickname,
            boolean isAnonymous,
            int commentCount,
            int viewCount,
            int likeCount,
            LocalDateTime createdAt
    ) {
        public static PostSummary from(Post post, int commentCount) {
            return new PostSummary(
                    post.getId(),
                    post.getTitle(),
                    post.getNickname(),
                    post.isAnonymous(),
                    commentCount,
                    post.getViewCount(),
                    post.getLikeCount(),
                    post.getCreatedAt()
            );
        }
    }
}
