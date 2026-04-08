package fittoring.application.chat.service.event;

import fittoring.domain.model.ChatMessage;

public record ChatMessagePersistedEvent(
        Long chatRoomId,
        Long senderId,
        Long opponentId,
        ChatMessage chatMessage
) {
}
