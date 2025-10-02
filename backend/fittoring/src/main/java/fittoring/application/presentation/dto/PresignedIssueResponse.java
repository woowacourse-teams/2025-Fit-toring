package fittoring.application.presentation.dto;

import java.time.LocalDateTime;

public record PresignedIssueResponse(
        String presignedUrl,
        LocalDateTime expiresAt
) {
}
