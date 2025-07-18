package fittoring.mentoring.presentation.api;

import fittoring.mentoring.business.service.MentoringReservationService;
import fittoring.mentoring.business.service.ReservationService;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.presentation.dto.ReservationCreateRequest;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
import jakarta.validation.Valid;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @PathVariable("mentoringId") Long mentoringId,
            @Valid @RequestBody ReservationCreateRequest requestBody
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        ReservationCreateDto reservationCreateDto = new ReservationCreateDto(
                mentoringId,
                requestBody.menteeName(),
                requestBody.menteePhone(),
                requestBody.content()
        );
        ReservationCreateResponse responseBody = mentoringReservationService.reserveMentoring(reservationCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseBody);
    }
}
