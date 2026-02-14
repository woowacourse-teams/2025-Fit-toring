package fittoring.application.chat.presentation.dto.response;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ChatMessageNotImageException;
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

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return from(chatMessage, null, null, null);
    }

    public static ChatMessageResponse from(ChatMessage chatMessage, Long tempId) {
        return from(chatMessage, tempId, null, null);
    }

    public static ChatMessageResponse from(
            ChatMessage chatMessage,
            Long tempId,
            String thumbnailUrl,
            String originalImageUrl
    ) {
        ChatMessageType type = chatMessage.getMessageType();
        if (type == ChatMessageType.IMAGE) {
            if(originalImageUrl == null){
                throw new ChatMessageNotImageException(BusinessErrorMessage.CHAT_MESSAGE_NOT_IMAGE.getMessage());
            }

            return new ChatMessageResponse(
                    chatMessage.getId(),
                    tempId,
                    chatMessage.getChatRoomId(),
                    chatMessage.getSenderId(),
                    null,
                    type,
                    thumbnailUrl,
                    originalImageUrl,
                    chatMessage.getCreatedAt()
            );
        }

        return new ChatMessageResponse(
                chatMessage.getId(),
                tempId,
                chatMessage.getChatRoomId(),
                chatMessage.getSenderId(),
                chatMessage.getContent(),
                type,
                null,
                null,
                chatMessage.getCreatedAt()
        );
    }
}
