package fittoring.mentoring.business.service.dto;

public record AdminReservationStatusUpdateDto(
    Long memberId,
    Long reservationId,
    String status
) {

    public static AdminReservationStatusUpdateDto of(
        Long memberId,
        Long reservationId,
        String status
    ) {
        return new AdminReservationStatusUpdateDto(memberId, reservationId, status);
    }
}
