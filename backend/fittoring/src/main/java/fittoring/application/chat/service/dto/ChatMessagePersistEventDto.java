package fittoring.application.chat.service.dto;

import fittoring.application.chat.presentation.dto.request.ChatTextMessageRequest;
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

    public static ChatMessagePersistEventDto text(
            String messageId,
            Long chatRoomId,
            Long senderId,
            ChatTextMessageRequest request,
            LocalDateTime requestedAt
    ) {
        return new ChatMessagePersistEventDto(
                messageId,
                chatRoomId,
                senderId,
                request.tempId(),
                request.content(),
                ChatMessageType.TEXT,
                requestedAt
        );
    }

    public static ChatMessagePersistEventDto image(
            String messageId,
            Long chatRoomId,
            Long senderId,
            Long tempId,
            String s3Key,
            LocalDateTime requestedAt
    ) {
        return new ChatMessagePersistEventDto(
                messageId,
                chatRoomId,
                senderId,
                tempId,
                s3Key,
                ChatMessageType.IMAGE,
                requestedAt
        );
    }
}
