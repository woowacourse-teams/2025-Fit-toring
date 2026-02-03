package fittoring.application.chat.presentation.dto.response;

import fittoring.domain.model.ChatMessage;
import java.time.LocalDateTime;

public record ChatRoomPreviewResponse(
        Long chatRoomId,
        String profileImageUrl,
        String opponentName,
        String reservationStatus,
        String lastChatContent,
        LocalDateTime lastChatCreatedAt
) {
    public static ChatRoomPreviewResponse of(Long chatRoomId,
                                             String profileImageUrl,
                                             String opponentName,
                                             String reservationStatus,
                                             ChatMessage lastMessage
                                             ) {
        if (lastMessage == null) {
            return new ChatRoomPreviewResponse(chatRoomId,
                    profileImageUrl,
                    opponentName,
                    reservationStatus,
                    null,
                    null
            );
        }
        return new ChatRoomPreviewResponse(chatRoomId, profileImageUrl, opponentName, reservationStatus, lastMessage.getContent(), lastMessage.getCreatedAt());
    }
}
