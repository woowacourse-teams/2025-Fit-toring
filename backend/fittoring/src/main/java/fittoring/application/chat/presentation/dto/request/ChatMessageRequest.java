package fittoring.application.chat.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
        @Size(min = 1, max = 2000, message = "메시지는 1자 이상 2000자 이하로 입력해야합니다.")
        @NotBlank
        String content,
        @NotNull
        Long tempId
) {
}
