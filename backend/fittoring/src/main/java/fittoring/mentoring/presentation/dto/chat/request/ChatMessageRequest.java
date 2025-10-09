package fittoring.mentoring.presentation.dto.chat.request;

public record ChatMessageRequest(
        String content,
        Long tempId
) {
}
