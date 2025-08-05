package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.repository.CategoryMentoringRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.presentation.dto.MemberReservationGetResponse;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final MentoringRepository mentoringRepository;
    private final ReservationRepository reservationRepository;
    private final CategoryMentoringRepository categoryMentoringRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ReservationCreateResponse createReservation(ReservationCreateDto dto) {
        Mentoring mentoring = mentoringRepository.findById(dto.mentoringId())
                .orElseThrow(() -> new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage()));
        Reservation savedReservation = reservationRepository.save(new Reservation(
                dto.menteeName(),
                dto.menteePhone(),
                dto.content(),
                mentoring
        ));
        return ReservationCreateResponse.from(savedReservation);
    }

    public List<MemberReservationGetResponse> findMemberReservations(Long memberId) {
        List<Reservation> memberReservations = reservationRepository.findByMemberId(memberId) // TODO: 멤버 이름 수정하기
            .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        return memberReservations.stream()
            .map(reservation -> {
                Mentoring mentoring = reservation.getMentoring();
                List<String> categories = categoryMentoringRepository.findTitleByMentoringId(mentoring.getId());
                boolean isReviewed = reviewRepository.existsByMentoringIdAndReviewerId(mentoring.getId(), memberId);
                return new MemberReservationGetResponse(
                    mentoring.getMentorName(),
                    mentoring.getPrice(),
                    mentoring.getCreatedAt(),
                    reservation.getCreatedAt(),
                    reservation.getStatus(),
                    categories,
                    isReviewed
                );
            })
            .toList();
    }
}
