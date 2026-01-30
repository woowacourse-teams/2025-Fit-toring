package fittoring.application.chat.presentation.dto.response;

import java.time.LocalDateTime;

public record ChatRoomPreviewResponse(
        Long chatRoomId,
        String profileImageUrl,
        String opponentName,
        String status,
        String lastChatContent,
        LocalDateTime createdAt
) {
    public static ChatRoomPreviewResponse of(Long chatRoomId, String profileImageUrl, String opponentName, String status) {
        return new ChatRoomPreviewResponse(chatRoomId, profileImageUrl, opponentName, status, null, null);
    }
}
