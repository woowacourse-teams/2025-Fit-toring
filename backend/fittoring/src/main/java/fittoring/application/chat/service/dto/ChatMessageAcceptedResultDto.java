package fittoring.application.chat.service.dto;

import fittoring.domain.model.ChatMessageType;
import java.time.LocalDateTime;

public record ChatMessageAcceptedResultDto(
        String messageId,
        Long tempId,
        Long chatRoomId,
        Long senderId,
        String content,
        ChatMessageType messageType,
        String thumbnailUrl,
        String originalImageUrl,
        LocalDateTime createdAt
) {

    public static ChatMessageAcceptedResultDto text(ChatMessagePersistEventDto event) {
        return new ChatMessageAcceptedResultDto(
                event.messageId(),
                event.tempId(),
                event.chatRoomId(),
                event.senderId(),
                event.content(),
                ChatMessageType.TEXT,
                null,
                null,
                event.requestedAt()
        );
    }
}
