package fittoring.application.community.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequest(
        @NotBlank(message = "댓글 내용은 필수 입력값입니다.") String content,
        String guestPassword
) {
}
