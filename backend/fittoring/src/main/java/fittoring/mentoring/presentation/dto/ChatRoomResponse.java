package fittoring.mentoring.presentation.dto;

public record ChatRoomResponse(
        Long mentoringId,
        String opponentName,
        String status
) {
}
