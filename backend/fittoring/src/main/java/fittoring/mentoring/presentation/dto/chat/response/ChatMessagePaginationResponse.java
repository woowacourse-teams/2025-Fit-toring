package fittoring.mentoring.presentation.dto.chat.response;

import java.util.List;

public record ChatMessagePaginationResponse(
        List<ChatMessageResponse> chatMessages,
        String nextCursorCode,
        boolean hasNext
) {
}
