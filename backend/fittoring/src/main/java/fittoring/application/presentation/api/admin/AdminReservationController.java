package fittoring.application.presentation.api.admin;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.application.business.service.ReservationService;
import fittoring.application.business.service.dto.MentoringReservationGetDto;
import fittoring.application.business.service.dto.AdminReservationStatusUpdateDto;
import fittoring.application.presentation.dto.AdminReservationDeleteDto;
import fittoring.application.presentation.dto.AdminReservationResponse;
import fittoring.application.presentation.dto.ReservationStatusUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminReservationController {

    private final ReservationService reservationService;

    @AuthRequired
    @GetMapping("/admin/mentorings/{mentoringId}/reservations")
    public ResponseEntity<List<AdminReservationResponse>> findMentoringReservations(
            @Login LoginInfo loginInfo,
            @PathVariable("mentoringId") Long mentoringId
    ) {
        MentoringReservationGetDto mentoringReservationGetDto
                = new MentoringReservationGetDto(loginInfo.memberId(), mentoringId);
        List<AdminReservationResponse> responseBody = reservationService.findMentoringReservationsWithAdminAuthorization(
                mentoringReservationGetDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @AuthRequired
    @PatchMapping("/admin/reservations/{reservationId}/status")
    public ResponseEntity<Void> updateStatus(
            @Login LoginInfo loginInfo,
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationStatusUpdateRequest request
    ) {
        AdminReservationStatusUpdateDto adminReservationStatusUpdateDto
                = AdminReservationStatusUpdateDto.of(loginInfo.memberId(), reservationId, request.status());
        reservationService.updateStatusWithAdminAuthorization(adminReservationStatusUpdateDto);
        return ResponseEntity.status(HttpStatus.OK)
            .build();
    }

    @AuthRequired
    @DeleteMapping("/admin/reservations/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @Login LoginInfo loginInfo,
            @PathVariable Long reservationId
    ) {
        AdminReservationDeleteDto adminReservationDeleteDto
                = new AdminReservationDeleteDto(loginInfo.memberId(), reservationId);
        reservationService.deleteReservationWithAdminAuthorization(adminReservationDeleteDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
