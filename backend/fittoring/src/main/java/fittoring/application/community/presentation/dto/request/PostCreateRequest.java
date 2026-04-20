package fittoring.application.community.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateRequest(
        @Size(max = 255, message = "게시글 제목은 255자 이하로 입력해야합니다.")
        @NotBlank(message = "게시글 제목은 필수 입력값입니다.") String title,
        @NotBlank(message = "게시글 내용은 필수 입력값입니다.") String content,
        boolean isAnonymous,
        @Size(max = 50, message = "닉네임은 50자 이하로 입력해야합니다.")
        @NotBlank(message = "비회원은 닉네임을 입력해주세요") String nickname,
        @NotBlank(message = "비회원은 비밀번호를 입력해주세요") String guestPassword
) {
}
