package fittoring.application.chat.presentation.dto.request;

import fittoring.domain.model.ChatMessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
        @NotBlank(message = "메시지 내용은 필수입니다.")
        @Size(max = 2000, message = "메시지는 2000자 이하로 입력해야합니다.")
        String content,
        @NotNull(message = "임시 ID는 필수 입력값입니다.")
        Long tempId,
        @NotNull(message = "메시지 타입은 필수 입력값입니다.")
        ChatMessageType messageType
) {
}
