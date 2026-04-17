package fittoring.application.community.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostCreateRequest(
        @NotBlank(message = "게시글 제목은 필수 입력값입니다.") String title,
        @NotBlank(message = "게시글 내용은 필수 입력값입니다.") String content,
        boolean isAnonymous,
        @NotBlank(message = "비회원은 닉네임을 입력해주세요") String nickname,
        @NotBlank(message = "비회원은 비밀번호를 입력해주세요") String guestPassword
) {
}
