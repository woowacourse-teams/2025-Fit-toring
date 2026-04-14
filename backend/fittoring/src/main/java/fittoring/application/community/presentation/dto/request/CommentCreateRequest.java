package fittoring.application.community.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
        @NotBlank String content,
        boolean isAnonymous,
        @NotBlank(message = "비회원은 닉네임을 입력해주세요") String nickname,
        @NotBlank(message = "비회원은 비밀번호를 입력해주세요") String guestPassword,
        Long rootId,
        Long parentId
) {
}
