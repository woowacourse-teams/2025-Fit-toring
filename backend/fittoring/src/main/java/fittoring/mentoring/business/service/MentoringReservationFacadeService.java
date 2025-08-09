package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MentoringReservationFacadeService {

    private final ReservationService reservationService;
    private final ReservationNotificationService reservationNotificationService;

    public ReservationCreateResponse reserveMentoring(ReservationCreateDto dto) {
        Reservation reservation = reservationService.createReservation(dto);
        reservationNotificationService.sendReservationSmsMessage(reservation);
        return ReservationCreateResponse.from(reservation);
    }

    public void updateReservationStatusAndSendSms(Long reservationId, String updatedStatus) {
        Reservation reservation = reservationService.updateStatus(reservationId, updatedStatus);
        reservationNotificationService.sendReservationStatusUpdateSmsMessage(reservation, updatedStatus);
    }
}
