package fittoring.admin.presentation.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record PostPreview(
        String nickname,
        OffsetDateTime scheduledAt,
        String title,
        String content,
        List<CommentPreview> comments
) {
}
