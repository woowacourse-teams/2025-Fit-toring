package fittoring.application.chat.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatImageMessageRequest(
        @NotBlank(message = "업로드 ID는 필수입니다.")
        String uploadId,
        @NotNull(message = "임시 ID는 필수 입력값입니다.")
        Long tempId
) implements ChatMessageRequest {
}
