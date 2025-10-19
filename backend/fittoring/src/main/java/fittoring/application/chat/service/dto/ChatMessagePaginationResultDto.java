package fittoring.application.chat.service.dto;

import fittoring.domain.model.ChatMessage;
import java.util.List;

public record ChatMessagePaginationResultDto(
        List<ChatMessage> chatMessages,
        String nextCursorCode,
        boolean hasNext
) {
}
