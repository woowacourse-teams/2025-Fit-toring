package fittoring.application.chat.presentation.dto.response;

import java.time.LocalDateTime;

public record ChatRoomPreviewResponse(
        Long chatRoomId,
        String profileImageUrl,
        String opponentName,
        String reservationStatus,
        String lastChatContent,
        LocalDateTime lastChatCreatedAt
) {
    public static ChatRoomPreviewResponse of(
            Long chatRoomId,
            String profileImageUrl,
            String opponentName,
            String reservationStatus,
            String lastChatContent,
            LocalDateTime lastChatCreatedAt
    ) {
        return new ChatRoomPreviewResponse(
                chatRoomId,
                profileImageUrl,
                opponentName,
                reservationStatus,
                lastChatContent,
                lastChatCreatedAt
        );
    }
}
