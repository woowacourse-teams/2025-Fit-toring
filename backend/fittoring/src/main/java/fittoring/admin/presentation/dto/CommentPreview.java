package fittoring.admin.presentation.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record CommentPreview(
        String nickname,
        OffsetDateTime scheduledAt,
        String content,
        List<CommentPreview> replies
) {
}
