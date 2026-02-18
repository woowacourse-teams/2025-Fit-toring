package fittoring.application.chat.presentation.dto.response;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record ChatWebSocketErrorResponse(
        int statusCode,
        String code,
        String message,
        LocalDateTime timestamp,
        String traceId,
        Long chatRoomId
) {

    public static ChatWebSocketErrorResponse of(
            int statusCode,
            String code,
            String message,
            String traceId,
            Long chatRoomId
    ) {
        return new ChatWebSocketErrorResponse(
                statusCode,
                code,
                message,
                LocalDateTime.now(ZoneId.of("Asia/Seoul")),
                traceId,
                chatRoomId
        );
    }
}
