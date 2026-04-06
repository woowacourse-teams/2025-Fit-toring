package fittoring.application.community.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequest(
        @NotBlank String content,
        String guestPassword
) {
}
