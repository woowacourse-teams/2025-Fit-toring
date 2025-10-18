package fittoring.mentoring.business.service.dto.chat;

import fittoring.mentoring.business.model.ChatMessage;
import java.util.List;

public record ChatMessagePaginationResultDto(
        List<ChatMessage> chatMessages,
        String nextCursorCode,
        boolean hasNext
) {
}
