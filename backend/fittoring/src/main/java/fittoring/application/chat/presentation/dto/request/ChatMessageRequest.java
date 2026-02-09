package fittoring.application.chat.presentation.dto.request;

import fittoring.domain.model.ChatMessageType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
        @Size(max = 2000, message = "메시지는 2000자 이하로 입력해야합니다.")
        String content,
        @NotNull(message = "임시 ID는 필수 입력값입니다.")
        Long tempId,
        @NotNull(message = "메시지 타입은 필수 입력값입니다.")
        ChatMessageType messageType,
        String baseName
) {

    public ChatMessageRequest {
        if (messageType == ChatMessageType.TEXT && (content == null || content.isBlank())) {
            throw new IllegalArgumentException("텍스트 메시지는 내용이 필수입니다.");
        }
        if (messageType == ChatMessageType.IMAGE && (baseName == null || baseName.isBlank())) {
            throw new IllegalArgumentException("이미지 메시지는 baseName이 필수입니다.");
        }
    }
}
