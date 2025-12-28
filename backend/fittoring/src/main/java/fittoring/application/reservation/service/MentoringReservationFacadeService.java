package fittoring.application.reservation.service;

import fittoring.application.mentoring.service.dto.ReservationInfo;
import fittoring.application.reservation.presentation.dto.response.ReservationCreateResponse;
import fittoring.application.reservation.service.dto.ReservationCreateDto;
import fittoring.domain.model.Reservation;
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

    public void approveAndSendSms(Long mentorId, Long reservationId) {
        ReservationInfo reservationInfo = reservationService.approve(mentorId, reservationId);
        reservationNotificationService.sendReservationApproveSmsMessage(
                reservationInfo.mentorName(),
                reservationInfo.content(),
                reservationInfo.menteePhone(),
                reservationInfo.chatRoomUrl()
        );
    }

    public void rejectAndSendSms(Long mentorId, Long reservationId) {
        ReservationInfo reservationInfo = reservationService.reject(mentorId, reservationId);
        reservationNotificationService.sendReservationRejectSmsMessage(
                reservationInfo.mentorName(),
                reservationInfo.menteePhone()
        );
    }
}
