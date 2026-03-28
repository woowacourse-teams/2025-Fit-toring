package fittoring.application.chat.presentation.dto.response;

import fittoring.application.chat.service.dto.ChatMessageAcceptedResultDto;
import fittoring.domain.model.ChatMessageType;
import java.time.LocalDateTime;

public record ChatMessageAcceptedResponse(
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

    public static ChatMessageAcceptedResponse from(ChatMessageAcceptedResultDto result) {
        return new ChatMessageAcceptedResponse(
                result.messageId(),
                result.tempId(),
                result.chatRoomId(),
                result.senderId(),
                result.content(),
                result.messageType(),
                result.thumbnailUrl(),
                result.originalImageUrl(),
                result.createdAt()
        );
    }
}
