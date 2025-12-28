package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.AdminReservationDeleteDto;
import fittoring.admin.presentation.dto.AdminReservationResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.AdminReservationQueryService;
import fittoring.admin.service.dto.AdminMentoringReservationDto;
import fittoring.admin.service.dto.AdminReservationStatusUpdateDto;
import fittoring.application.reservation.presentation.dto.request.ReservationStatusUpdateRequest;
import fittoring.application.reservation.service.ReservationService;
import fittoring.config.auth.AuthRequired;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminReservationController {

    private final AdminReservationQueryService reservationQueryService;
    private final ReservationService reservationCommandService;

    @AuthRequired
    @GetMapping("/admin/mentorings/{mentoringId}/reservations")
    public ResponseEntity<PageResult<AdminReservationResponse>> findMentoringReservations(
            @PathVariable("mentoringId") Long mentoringId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        AdminMentoringReservationDto mentoringReservationGetDto = new AdminMentoringReservationDto(
                mentoringId,
                page,
                size
        );
        PageResult<AdminReservationResponse> responseBody = reservationQueryService.findMentoringReservationsForAdmin(
                mentoringReservationGetDto
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @AuthRequired
    @PatchMapping("/admin/reservations/{reservationId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationStatusUpdateRequest request
    ) {
        AdminReservationStatusUpdateDto adminReservationStatusUpdateDto =
                AdminReservationStatusUpdateDto.of(reservationId, request.status());
        reservationCommandService.updateStatusWithAdminAuthorization(adminReservationStatusUpdateDto);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @AuthRequired
    @DeleteMapping("/admin/reservations/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long reservationId
    ) {
        AdminReservationDeleteDto adminReservationDeleteDto = new AdminReservationDeleteDto(reservationId);
        reservationCommandService.deleteReservationWithAdminAuthorization(adminReservationDeleteDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
