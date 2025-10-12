package fittoring.mentoring.business.service.dto.chat;

import fittoring.mentoring.business.model.ChatMessage;
import java.util.List;

public record ChatMessagePaginationResult(
        List<ChatMessage> chatMessages,
        String nextCursorCode,
        boolean hasNext
) {
}
