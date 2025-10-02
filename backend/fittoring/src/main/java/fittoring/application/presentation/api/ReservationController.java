package fittoring.application.presentation.api;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.application.service.MentoringReservationFacadeService;
import fittoring.application.service.ReservationService;
import fittoring.application.service.dto.MentorMentoringReservationResponse;
import fittoring.application.service.dto.PhoneNumberResponse;
import fittoring.application.service.dto.ReservationCreateDto;
import fittoring.application.presentation.dto.ParticipatedReservationResponse;
import fittoring.application.presentation.dto.ReservationCreateRequest;
import fittoring.application.presentation.dto.ReservationCreateResponse;
import fittoring.application.presentation.dto.ReservationStatusUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    @PatchMapping("/reservations/{reservationId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationStatusUpdateRequest request
    ) {
        mentoringReservationFacadeService.updateReservationStatusAndSendSms(reservationId, request.status());
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
