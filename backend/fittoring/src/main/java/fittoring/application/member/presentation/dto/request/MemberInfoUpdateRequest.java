package fittoring.application.member.presentation.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;

public record MemberInfoUpdateRequest(
        @Nullable
        @Pattern(regexp = "^(?!\\s*$).+", message = "이름은 비어 있을 수 없습니다.")
        String name,
        @Nullable
        @Pattern(regexp = "^(?!\\s*$).+", message = "성별은 비어 있을 수 없습니다.")
        String gender,
        @Nullable
        @Pattern(regexp = "^(?!\\s*$).+", message = "비밀번호는 비어 있을 수 없습니다.")
        String password,
        @Nullable
        @Pattern(regexp = "^(?!\\s*$).+", message = "전화번호는 비어 있을 수 없습니다.")
        String phoneNumber
) {
}
