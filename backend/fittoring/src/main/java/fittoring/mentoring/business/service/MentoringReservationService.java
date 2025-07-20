package fittoring.mentoring.business.service;

import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.business.service.dto.SmsReservationMessageDto;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MentoringReservationService {

    private final ReservationService reservationService;
    private final SmsRestClientService smsRestClientService;

    public ReservationCreateResponse reserveMentoring(ReservationCreateDto dto) {
        ReservationCreateResponse response = reservationService.createReservation(dto);
        String smsMessage = reservationService.createSmsReservationMessage(new SmsReservationMessageDto(
                dto.menteeName(),
                dto.menteePhone(),
                dto.content()
        ));
        smsRestClientService.sendSms(response.mentorPhone(), smsMessage);
        return response;
    }
}
