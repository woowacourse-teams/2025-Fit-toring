package fittoring.application.community.presentation.dto.response;

import fittoring.application.community.service.dto.PostPaginationResult;
import fittoring.domain.model.Post;
import java.time.LocalDateTime;
import java.util.List;

public record PostListResponse(
        List<PostSummary> posts,
        String nextCursorCode,
        boolean hasNext
) {

    public static PostListResponse from(PostPaginationResult result) {
        return new PostListResponse(
                result.posts().stream()
                        .map(PostSummary::from)
                        .toList(),
                result.nextCursorCode(),
                result.hasNext()
        );
    }

    public record PostSummary(
            Long id,
            String title,
            String nickname,
            boolean isAnonymous,
            LocalDateTime createdAt
    ) {
        public static PostSummary from(Post post) {
            return new PostSummary(
                    post.getId(),
                    post.getTitle(),
                    post.getNickname(),
                    post.isAnonymous(),
                    post.getCreatedAt()
            );
        }
    }
}
