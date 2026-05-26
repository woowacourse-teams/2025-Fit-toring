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
            String content,
            String nickname,
            boolean isAnonymous,
            int commentCount,
            int viewCount,
            int likeCount,
            LocalDateTime createdAt
    ) {
        private static final int CONTENT_PREVIEW_LENGTH = 50;

        public static PostSummary from(Post post, int commentCount) {
            return new PostSummary(
                    post.getId(),
                    post.getTitle(),
                    createPreview(post.getContent()),
                    post.getNickname(),
                    post.isAnonymous(),
                    commentCount,
                    post.getViewCount(),
                    post.getLikeCount(),
                    post.getCreatedAt()
            );
        }

        private static String createPreview(String content) {
            String normalized = content.strip().replaceAll("\\s+", " ");

            if (normalized.length() <= CONTENT_PREVIEW_LENGTH) {
                return normalized;
            }

            return normalized.substring(0, CONTENT_PREVIEW_LENGTH) + "...";
        }
    }
}
