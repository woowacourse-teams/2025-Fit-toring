package fittoring.application.community.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
        @NotBlank String content,
        boolean isAnonymous,
        String nickname,
        String guestPassword,
        Long rootId,
        Long parentId
) {
}
