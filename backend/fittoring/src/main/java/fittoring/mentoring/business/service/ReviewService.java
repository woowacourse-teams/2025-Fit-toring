package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MemberNotFoundException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.ReservationNotFoundException;
import fittoring.mentoring.business.exception.ReviewAlreadyExistsException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Review;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.repository.ReviewRepository;
import fittoring.mentoring.business.service.dto.ReviewCreateDto;
import fittoring.mentoring.presentation.dto.ReviewCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final MentoringRepository mentoringRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ReviewCreateResponse createReview(ReviewCreateDto dto) {
        Long mentoringId = dto.mentoringId();
        Long reviewerId = dto.reviewerId();
        Mentoring mentoring = mentoringRepository.findById(mentoringId)
            .orElseThrow(() -> new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage()));
        Member reviewer = memberRepository.findById(reviewerId)
            .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        validateReserved(mentoringId, reviewerId);
        validateDuplicated(mentoringId, reviewerId);
        Review savedReview = reviewRepository.save(new Review(dto.rating(), dto.content(), mentoring, reviewer));
        return ReviewCreateResponse.of(savedReview);
    }

    private void validateReserved(Long mentoringId, Long reviewerId) {
        if (reservationRepository.existsByMentoringIdAndMenteeId(mentoringId, reviewerId)) {
            return;
        }
        throw new ReservationNotFoundException(BusinessErrorMessage.REVIEWING_RESERVATION_NOT_FOUND.getMessage());
    }

    private void validateDuplicated(Long mentoringId, Long reviewerId) {
        if (reviewRepository.existsByMentoringIdAndReviewerId(mentoringId, reviewerId)) {
            throw new ReviewAlreadyExistsException(BusinessErrorMessage.DUPLICATED_REVIEW.getMessage());
        }
    }
}