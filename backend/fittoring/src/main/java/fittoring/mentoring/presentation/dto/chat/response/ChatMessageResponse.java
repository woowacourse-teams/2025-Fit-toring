package fittoring.mentoring.presentation.dto.chat.response;

import fittoring.mentoring.business.model.ChatMessage;
import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long chatMessageId,
        Long tempId,
        Long chatRoomId,
        Long senderId,
        String content,
        LocalDateTime createdAt
) {

    public static ChatMessageResponse from(ChatMessage chatMessage, Long tempId) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getChatRoomId(),
                tempId,
                chatMessage.getSenderId(),
                chatMessage.getContent(),
                chatMessage.getCreatedAt()
        );
    }
}
