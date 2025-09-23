package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.ForbiddenException;
import fittoring.mentoring.business.exception.MentorAndMenteeIsSameException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.exception.ReservationNotFoundException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.MemberRole;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.repository.ReviewRepository;
import fittoring.mentoring.business.service.dto.AdminReservationStatusUpdateDto;
import fittoring.mentoring.business.service.dto.MentorMentoringReservationResponse;
import fittoring.mentoring.business.service.dto.MentoringReservationGetDto;
import fittoring.mentoring.business.service.dto.ParticipatedReservationView;
import fittoring.mentoring.business.service.dto.PhoneNumberResponse;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.presentation.dto.AdminReservationDeleteDto;
import fittoring.mentoring.presentation.dto.AdminReservationResponse;
import fittoring.mentoring.presentation.dto.ParticipatedReservationResponse;
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
    private final ReviewRepository reviewRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public Reservation createReservation(ReservationCreateDto dto) {
        Reservation reservation = createReservationEntity(dto);
        return reservationRepository.save(reservation);
    }

    private Reservation createReservationEntity(ReservationCreateDto dto) {
        Mentoring mentoring = getMentoring(dto.mentoringId());
        validateNotMyMentoring(mentoring, dto.menteeId());
        Member mentee = getMember(dto.menteeId());
        return new Reservation(
                dto.content(),
                Status.PENDING,
                mentoring,
                mentee
        );
    }

    private Mentoring getMentoring(Long mentoringId) {
        return mentoringRepository.findById(mentoringId)
                .orElseThrow(
                        () -> new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage()));
    }

    private void validateNotMyMentoring(Mentoring mentoring, Long menteeId) {
        if (mentoring.isCreatedByMember(menteeId)) {
            throw new MentorAndMenteeIsSameException(BusinessErrorMessage.MENTOR_AND_MENTEE_IS_SAME.getMessage());
        }
    }

    private Member getMember(Long menteeId) {
        return memberRepository.findById(menteeId)
                .orElseThrow(
                        () -> new NotFoundMemberException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
    }

    @Transactional(readOnly = true)
    public List<MentorMentoringReservationResponse> getReservationsByMentor(Long mentorId) {
        List<Reservation> reservations = reservationRepository.findAllByMentorId(mentorId);
        return getMentorMentoringReservationResponses(reservations);
    }

    private List<MentorMentoringReservationResponse> getMentorMentoringReservationResponses(
            List<Reservation> reservations) {
        return reservations.stream()
                .map(MentorMentoringReservationResponse::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public PhoneNumberResponse getPhone(Long reservationId) {
        Reservation reservation = getReservation(reservationId);
        return new PhoneNumberResponse(reservation.getMenteePhone());
    }

    @Transactional(readOnly = true)
    public List<ParticipatedReservationResponse> findMemberReservations(Long memberId) {
        List<ParticipatedReservationView> views = reservationRepository.findMemberReservationsView(memberId);
        return views.stream()
                .map(v -> new ParticipatedReservationResponse(
                        v.getReservationId(),
                        v.getMentoringId(),
                        v.getMentorName(),
                        v.getMentorProfileImage(),
                        v.getReservedAt(),
                        v.getContent(),
                        v.getStatus(),
                        v.getIsReviewed()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminReservationResponse> findMentoringReservationsWithAdminAuthorization(
            MentoringReservationGetDto dto) {
        checkAdminAuthority(dto.memberId());
        List<Reservation> reservations = reservationRepository.findAllByMentoringId(dto.mentoringId());
        return reservations.stream()
                .map(reservation -> new AdminReservationResponse(
                        reservation.getId(),
                        reservation.getMenteeName(),
                        reservation.getCreatedAt().toLocalDate(),
                        reservation.getStatus(),
                        reservation.getContent()
                ))
                .toList();
    }

    private void checkAdminAuthority(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        if (MemberRole.isNotAdmin(member.getRole())) {
            throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
        }
    }

    @Transactional
    public Reservation updateStatus(Long reservationId, String updateStatus) {
        Reservation reservation = getReservation(reservationId);
        Status status = Status.of(updateStatus);
        reservation.changeStatus(status);
        return reservation;
    }

    private Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(
                        () -> new ReservationNotFoundException(
                                BusinessErrorMessage.RESERVATION_NOT_FOUND.getMessage())
                );
    }

    @Transactional
    public void updateStatusWithAdminAuthorization(AdminReservationStatusUpdateDto adminReservationStatusUpdateDto) {
        checkAdminAuthority(adminReservationStatusUpdateDto.memberId());
        Reservation reservation = getReservation(adminReservationStatusUpdateDto.reservationId());
        Status status = Status.of(adminReservationStatusUpdateDto.status());
        reservation.changeStatusWithoutValidation(status);
    }

    @Transactional
    public void deleteReservationWithAdminAuthorization(AdminReservationDeleteDto adminReservationDeleteDto) {
        checkAdminAuthority(adminReservationDeleteDto.memberId());
        Reservation reservation = getReservation(adminReservationDeleteDto.reservationId());
        reviewRepository.deleteByReservation(reservation);
        reservationRepository.delete(reservation);
    }
}
