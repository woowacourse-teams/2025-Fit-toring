package fittoring.mentoring.business.service;

import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MentoringReservationService {

    private final ReservationService reservationService;
    private final SmsRestClientService smsRestClientService;

    public ReservationCreateResponse reserveMentoring(ReservationCreateDto dto)
            throws NoSuchAlgorithmException, InvalidKeyException {
        ReservationCreateResponse response = reservationService.createReservation(dto);
        smsRestClientService.sendSms(response.mentorPhone(), dto.content());
        return response;
    }
}
