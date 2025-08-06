package fittoring.mentoring.presentation.api;

import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.service.MentoringReservationService;
import fittoring.mentoring.business.service.ReservationService;
import fittoring.mentoring.business.service.dto.MentorMentoringReservationResponse;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.presentation.dto.ReservationCreateRequest;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReservationController {

    private final MentoringReservationService mentoringReservationService;
    private final ReservationService reservationService;

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
        ReservationCreateResponse responseBody = mentoringReservationService.reserveMentoring(
                reservationCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseBody);
    }

    @GetMapping("/mentorings/mine/reservations")
    public ResponseEntity<List<MentorMentoringReservationResponse>> getReservationsByMentor(
            @Login LoginInfo loginInfo
    ) {
        List<MentorMentoringReservationResponse> response = reservationService.getReservationsByMentor(
                loginInfo.memberId()
        );
        return ResponseEntity.ok(response);
    }
}
