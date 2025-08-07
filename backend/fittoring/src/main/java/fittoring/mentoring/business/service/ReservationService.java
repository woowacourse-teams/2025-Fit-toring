package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.exception.NotFoundReservationException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.service.dto.MentorMentoringReservationResponse;
import fittoring.mentoring.business.service.dto.PhoneNumberResponse;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import java.util.ArrayList;
import java.util.List;
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

    @Transactional(readOnly = true)
    public List<MentorMentoringReservationResponse> getReservationsByMentor(Long mentorId) {
        List<Mentoring> mentoringsByMentor = mentoringRepository.findAllByMentorId(mentorId);
        List<Reservation> reservations = findReservation(mentoringsByMentor);
        return getMentorMentoringReservationResponses(reservations);
    }

    private List<Reservation> findReservation(List<Mentoring> mentoringsByMentor) {
        List<Reservation> reservations = new ArrayList<>();
        for (Mentoring mentoring : mentoringsByMentor) {
            List<Reservation> mentorings = reservationRepository.findAllByMentoringId(mentoring.getId());
            reservations.addAll(mentorings);
        }
        return reservations;
    }

    private List<MentorMentoringReservationResponse> getMentorMentoringReservationResponses(
            List<Reservation> reservations) {
        return reservations.stream()
                .map(MentorMentoringReservationResponse::of)
                .toList();
    }

    @Transactional
    public void updateStatus(Long reservationId, String updateStatus) {
        Reservation reservation = getReservation(reservationId);
        reservation.changeStatus(Status.valueOf(updateStatus));
    }

    private Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(
                        () -> new NotFoundReservationException(
                                BusinessErrorMessage.RESERVATION_NOT_FOUND.getMessage())
                );
    }

    @Transactional(readOnly = true)
    public PhoneNumberResponse getPhone(Long reservationId) {
        Reservation reservation = getReservation(reservationId);
        return new PhoneNumberResponse(reservation.getMenteePhone());
    }
}
