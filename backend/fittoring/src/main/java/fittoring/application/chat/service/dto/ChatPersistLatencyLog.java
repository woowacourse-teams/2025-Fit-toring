package fittoring.application.chat.service.dto;

public record ChatPersistLatencyLog(
        String eventName,
        String messageId,
        Long chatRoomId,
        Long senderId,
        Long opponentId,
        String messageType,
        long dbMs,
        long imageMs,
        long notificationMs,
        long totalMs
) {
}
