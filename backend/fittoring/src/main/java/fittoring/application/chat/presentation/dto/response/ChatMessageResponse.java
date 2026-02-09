package fittoring.application.chat.presentation.dto.response;

import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.ChatMessageType;
import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long chatMessageId,
        Long tempId,
        Long chatRoomId,
        Long senderId,
        String content,
        ChatMessageType messageType,
        String thumbnailUrl,
        String originalImageUrl,
        LocalDateTime createdAt
) {

    public static ChatMessageResponse ofText(ChatMessage chatMessage, Long tempId) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                tempId,
                chatMessage.getChatRoomId(),
                chatMessage.getSenderId(),
                chatMessage.getContent(),
                chatMessage.getMessageType(),
                null,
                null,
                chatMessage.getCreatedAt()
        );
    }

    public static ChatMessageResponse ofTextHistory(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                null,
                chatMessage.getChatRoomId(),
                chatMessage.getSenderId(),
                chatMessage.getContent(),
                chatMessage.getMessageType(),
                null,
                null,
                chatMessage.getCreatedAt()
        );
    }

    public static ChatMessageResponse ofImage(
            ChatMessage chatMessage,
            Long tempId,
            String thumbnailUrl,
            String originalImageUrl
    ) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                tempId,
                chatMessage.getChatRoomId(),
                chatMessage.getSenderId(),
                null,
                ChatMessageType.IMAGE,
                thumbnailUrl,
                originalImageUrl,
                chatMessage.getCreatedAt()
        );
    }
}
