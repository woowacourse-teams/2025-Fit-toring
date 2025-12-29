package fittoring.application.reservation.presentation;

import fittoring.application.mentoring.service.dto.MentorMentoringReservationResponse;
import fittoring.application.reservation.presentation.dto.request.ReservationCreateRequest;
import fittoring.application.reservation.presentation.dto.response.ParticipatedReservationResponse;
import fittoring.application.reservation.presentation.dto.response.PhoneNumberResponse;
import fittoring.application.reservation.presentation.dto.response.ReservationCreateResponse;
import fittoring.application.reservation.service.MentoringReservationFacadeService;
import fittoring.application.reservation.service.ReservationService;
import fittoring.application.reservation.service.dto.ReservationCreateDto;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReservationController {

    private final MentoringReservationFacadeService mentoringReservationFacadeService;
    private final ReservationService reservationService;

    @AuthRequired
    @PostMapping("/mentorings/{mentoringId}/reservation")
    public ResponseEntity<ReservationCreateResponse> createReservation(
            @Login LoginInfo loginInfo,
            @PathVariable("mentoringId") Long mentoringId,
            @Valid @RequestBody ReservationCreateRequest requestBody
    ) {
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                loginInfo.memberId(),
                mentoringId,
                requestBody
        );
        ReservationCreateResponse responseBody = mentoringReservationFacadeService.reserveMentoring(
                reservationCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseBody);
    }

    @AuthRequired
    @GetMapping("/reservations/participated")
    public ResponseEntity<List<ParticipatedReservationResponse>> findParticipatedReservation(
            @Login LoginInfo loginInfo
    ) {
        List<ParticipatedReservationResponse> responseBody = reservationService.findMemberReservations(
                loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @AuthRequired
    @GetMapping("/mentorings/mine/reservations")
    public ResponseEntity<List<MentorMentoringReservationResponse>> getReservationsByMentor(
            @Login LoginInfo loginInfo
    ) {
        List<MentorMentoringReservationResponse> response = reservationService.getReservationsByMentor(
                loginInfo.memberId()
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @AuthRequired
    @PatchMapping("/reservations/{reservationId}/approve")
    public ResponseEntity<Void> approveStatus(
            @Login LoginInfo loginInfo,
            @PathVariable Long reservationId
    ) {
        mentoringReservationFacadeService.approveAndSendSms(loginInfo.memberId(), reservationId);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @AuthRequired
    @PatchMapping("/reservations/{reservationId}/reject")
    public ResponseEntity<Void> rejectStatus(
            @Login LoginInfo loginInfo,
            @PathVariable Long reservationId
    ) {
        mentoringReservationFacadeService.rejectAndSendSms(loginInfo.memberId(), reservationId);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @AuthRequired
    @GetMapping("/reservations/{reservationId}/phone")
    public ResponseEntity<PhoneNumberResponse> getPhone(@PathVariable Long reservationId) {
        PhoneNumberResponse response = reservationService.getPhone(reservationId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
