package fittoring.application.community.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostCreateRequest(
        @NotBlank String title,
        @NotBlank String content,
        boolean isAnonymous,
        String nickname,
        String guestPassword
) {
}
