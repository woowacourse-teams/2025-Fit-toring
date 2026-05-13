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
}
