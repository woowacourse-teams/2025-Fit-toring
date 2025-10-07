package fittoring.application.reservation.service;

import fittoring.domain.model.Reservation;
import fittoring.application.reservation.service.dto.ReservationCreateDto;
import fittoring.application.reservation.presentation.dto.response.ReservationCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void updateReservationStatusAndSendSms(Long reservationId, String updatedStatus) {
        Reservation reservation = reservationService.updateStatus(reservationId, updatedStatus);
        reservationNotificationService.sendReservationStatusUpdateSmsMessage(reservation, updatedStatus);
    }
}
