package fittoring.application.chat.presentation.dto.response;

import java.time.LocalDateTime;

public record ChatImagePresignedResponse(
        String uploadId,
        String presignedUrl,
        LocalDateTime expiresAt
) {
}
