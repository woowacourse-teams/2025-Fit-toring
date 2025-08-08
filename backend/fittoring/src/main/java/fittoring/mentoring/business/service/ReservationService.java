package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.repository.CategoryMentoringRepository;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.repository.ReviewRepository;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.presentation.dto.MemberReservationGetResponse;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
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
    public ReservationCreateResponse createReservation(ReservationCreateDto dto) {
        Mentoring mentoring = mentoringRepository.findById(dto.mentoringId())
                .orElseThrow(
                        () -> new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage()));
        Member mentee = memberRepository.findById(dto.menteeId())
                .orElseThrow(
                        () -> new NotFoundMemberException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        Reservation savedReservation = reservationRepository.save(new Reservation(
                dto.content(),
                mentoring,
                mentee
        ));
        return ReservationCreateResponse.from(savedReservation);
    }

    @Transactional(readOnly = true)
    public List<MemberReservationGetResponse> findMemberReservations(Long memberId) {
        List<Reservation> memberReservations = reservationRepository.findAllByMenteeId(memberId);
        return memberReservations.stream()
            .map(this::generateMemberReservationGetResponse)
            .toList();
    }

    private MemberReservationGetResponse generateMemberReservationGetResponse(Reservation reservation) {
        Mentoring mentoring = reservation.getMentoring();
        String mentorProfileImage = findProfileImageUrl(mentoring.getId());
        List<String> categories = categoryMentoringRepository.findTitleByMentoringId(mentoring.getId());
        boolean isReviewed = reviewRepository.existsByReservationId(reservation.getId());
        return new MemberReservationGetResponse(
            reservation.getId(),
            mentoring.getId(),
            mentoring.getMentor().getName(),
            mentorProfileImage,
            mentoring.getPrice(),
            reservation.getCreatedAt().toLocalDate(),
            categories,
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
