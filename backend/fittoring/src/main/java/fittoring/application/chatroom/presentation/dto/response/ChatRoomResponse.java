package fittoring.application.chatroom.presentation.dto.response;

public record ChatRoomResponse(
        Long mentoringId,
        String opponentName,
        String myRole,
        String status
) {
}
