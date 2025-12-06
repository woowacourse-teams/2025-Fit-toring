package fittoring.application.reservation.service;

import fittoring.application.mentoring.service.dto.ReservationInfo;
import fittoring.application.reservation.presentation.dto.response.ReservationCreateResponse;
import fittoring.application.reservation.service.dto.ReservationCreateDto;
import fittoring.domain.model.Reservation;
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
        ReservationInfo reservationInfo = reservationService.updateStatus(reservationId, updatedStatus);
        reservationNotificationService.sendReservationStatusUpdateSmsMessage(
                reservationInfo.reservation(),
                reservationInfo.reservation().getStatus(),
                reservationInfo.chatRoomUrl()
        );
    }
}
