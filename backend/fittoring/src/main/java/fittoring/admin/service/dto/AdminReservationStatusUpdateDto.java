package fittoring.admin.service.dto;

public record AdminReservationStatusUpdateDto(
        Long reservationId,
        String status
) {

    public static AdminReservationStatusUpdateDto of(
            Long reservationId,
            String status
    ) {
        return new AdminReservationStatusUpdateDto(reservationId, status);
    }
}
