package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final MentoringRepository mentoringRepository;
    private final ReservationRepository reservationRepository;
    private final SmsRestClientService smsRestClientService;

    @Transactional
    public ReservationCreateResponse createReservation(ReservationCreateDto dto)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mentoring mentoring = mentoringRepository.findById(dto.mentoringId())
                // TODO: custom exception 필요
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멘토링 ID 입니다:" + dto.mentoringId()));
        Reservation savedReservation = reservationRepository.save(new Reservation(
                dto.menteeName(),
                dto.menteePhone(),
                dto.content(),
                mentoring
        ));
        smsRestClientService.sendSms(dto.menteePhone(), dto.content());
        return ReservationCreateResponse.from(savedReservation);
    }
}
