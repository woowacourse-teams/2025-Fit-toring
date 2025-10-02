package fittoring.application.image.presentation.dto.response;

import java.time.LocalDateTime;

public record PresignedIssueResponse(
        String presignedUrl,
        LocalDateTime expiresAt
) {
}
