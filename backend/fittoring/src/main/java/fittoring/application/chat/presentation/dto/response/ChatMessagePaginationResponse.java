package fittoring.application.chat.presentation.dto.response;

import java.util.List;

public record ChatMessagePaginationResponse(
        List<ChatMessageResponse> chatMessages,
        String nextCursorCode,
        boolean hasNext
) {
}
