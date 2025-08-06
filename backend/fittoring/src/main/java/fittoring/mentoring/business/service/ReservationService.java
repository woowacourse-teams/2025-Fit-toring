package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final MentoringRepository mentoringRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Reservation createReservation(ReservationCreateDto dto) {
        Reservation reservation = createReservationEntity(dto);
        return reservationRepository.save(reservation);
    }

    private Reservation createReservationEntity(ReservationCreateDto dto) {
        Mentoring mentoring = getMentoring(dto.mentoringId());
        Member mentee = getMember(dto.menteeId());
        return new Reservation(
                dto.content(),
                mentoring,
                mentee,
                Status.PENDING
        );
    }

    private Mentoring getMentoring(Long mentoringId) {
        return mentoringRepository.findById(mentoringId)
                .orElseThrow(
                        () -> new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage()));
    }

    private Member getMember(Long menteeId) {
        return memberRepository.findById(menteeId)
                .orElseThrow(
                        () -> new NotFoundMemberException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
    }
}
