package fittoring.application.chat.presentation.dto.response;

public record ChatRoomResponse(
        Long mentoringId,
        String opponentName,
        String myRole,
        String status
) {
}
