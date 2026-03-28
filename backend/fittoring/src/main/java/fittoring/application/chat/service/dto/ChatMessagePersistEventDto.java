package fittoring.application.chat.service.dto;

import fittoring.application.chat.presentation.dto.request.ChatMessageRequest;
import fittoring.domain.model.ChatMessageType;
import java.time.LocalDateTime;

public record ChatMessagePersistEventDto(
        String messageId,
        Long chatRoomId,
        Long senderId,
        Long tempId,
        String content,
        ChatMessageType messageType,
        LocalDateTime requestedAt
) {

    public static ChatMessagePersistEventDto of(
            String messageId,
            Long chatRoomId,
            Long senderId,
            ChatMessageRequest request,
            String content,
            LocalDateTime requestedAt
    ) {
        return new ChatMessagePersistEventDto(
                messageId,
                chatRoomId,
                senderId,
                request.tempId(),
                content,
                request.messageType(),
                requestedAt
        );
    }
}
