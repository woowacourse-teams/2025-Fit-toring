package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.exception.ReservationNotFoundException;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.repository.CategoryMentoringRepository;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.repository.ReviewRepository;
import fittoring.mentoring.business.service.dto.MentorMentoringReservationResponse;
import fittoring.mentoring.business.service.dto.PhoneNumberResponse;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.presentation.dto.ParticipatedReservationResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final MentoringRepository mentoringRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final CategoryMentoringRepository categoryMentoringRepository;
    private final ReviewRepository reviewRepository;
    private final ImageRepository imageRepository;

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

    @Transactional(readOnly = true)
    public PhoneNumberResponse getPhone(Long reservationId) {
        Reservation reservation = getReservation(reservationId);
        return new PhoneNumberResponse(reservation.getMenteePhone());
    }

    @Transactional(readOnly = true)
    public List<ParticipatedReservationResponse> findMemberReservations(Long memberId) {
        List<Reservation> memberReservations = reservationRepository.findAllByMenteeId(memberId);
        return memberReservations.stream()
                .map(this::generateParticipatedReservationResponse)
                .toList();
    }

    private ParticipatedReservationResponse generateParticipatedReservationResponse(Reservation reservation) {
        Mentoring mentoring = reservation.getMentoring();
        String mentorProfileImage = findProfileImageUrl(mentoring.getId());
        List<String> categoryTitles = categoryMentoringRepository.findTitleByMentoringId(mentoring.getId());
        boolean isReviewed = reviewRepository.existsByReservationId(reservation.getId());
        return new ParticipatedReservationResponse(
                reservation.getId(),
                mentoring.getId(),
                mentoring.getMentor().getName(),
                mentorProfileImage,
                mentoring.getPrice(),
                reservation.getCreatedAt().toLocalDate(),
                categoryTitles,
                isReviewed
        );
    }

    private String findProfileImageUrl(Long relationId) {
        Optional<Image> image = imageRepository.findByImageTypeAndRelationId(
                ImageType.MENTORING_PROFILE, relationId);
        return image.map(Image::getUrl)
                .orElse(null);
    }
}
