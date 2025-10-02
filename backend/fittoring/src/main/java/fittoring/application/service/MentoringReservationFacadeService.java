package fittoring.application.service;

import fittoring.domain.model.Reservation;
import fittoring.application.service.dto.ReservationCreateDto;
import fittoring.application.presentation.dto.ReservationCreateResponse;
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
