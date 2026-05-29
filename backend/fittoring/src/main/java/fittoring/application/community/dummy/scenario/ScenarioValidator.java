package fittoring.application.community.dummy.scenario;

import java.time.OffsetDateTime;

public final class ScenarioValidator {

    private static final int MAX_DEPTH = 9;

    private ScenarioValidator() {
    }

    public static void validate(ScenarioFile file) {
        if (file.scenarios() == null || file.scenarios().isEmpty()) {
            throw new IllegalArgumentException("시나리오는 1개 이상이어야 합니다");
        }
        for (Scenario scenario : file.scenarios()) {
            validatePost(scenario.post());
            for (ScenarioComment root : scenario.comments()) {
                validateComment(root, scenario.post().scheduledAt(), 1);
            }
        }
    }

    private static void validatePost(ScenarioPost post) {
        requireNotBlank(post.nickname(), "게시글 nickname은 필수입니다");
        requireNotBlank(post.title(), "게시글 title은 필수입니다");
        requireNotBlank(post.content(), "게시글 content은 필수입니다");
        if (post.scheduledAt() == null) {
            throw new IllegalArgumentException("게시글 scheduled_at은 필수입니다");
        }
    }

    private static void validateComment(ScenarioComment comment, OffsetDateTime parentAt, int depth) {
        if (depth > MAX_DEPTH) {
            throw new IllegalArgumentException("댓글 트리 깊이는 " + MAX_DEPTH + "을 초과할 수 없습니다");
        }
        requireNotBlank(comment.nickname(), "댓글 nickname은 필수입니다");
        requireNotBlank(comment.content(), "댓글 content은 필수입니다");
        if (comment.scheduledAt() == null) {
            throw new IllegalArgumentException("댓글 scheduled_at은 필수입니다");
        }
        if (comment.scheduledAt().isBefore(parentAt)) {
            throw new IllegalArgumentException(
                    "댓글 scheduled_at(" + comment.scheduledAt()
                            + ")은 부모(" + parentAt + ")보다 빠를 수 없습니다");
        }
        for (ScenarioComment reply : comment.replies()) {
            validateComment(reply, comment.scheduledAt(), depth + 1);
        }
    }

    private static void requireNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
