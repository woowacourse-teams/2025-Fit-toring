package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.ReservationNotFoundException;
import fittoring.mentoring.business.exception.ReviewAlreadyExistsException;
import fittoring.mentoring.business.exception.ReviewNotFoundException;
import fittoring.mentoring.business.exception.ReviewerNotSameException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Review;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.repository.ReviewRepository;
import fittoring.mentoring.business.service.dto.MemberReviewGetDto;
import fittoring.mentoring.business.service.dto.ReviewCreateDto;
import fittoring.mentoring.business.service.dto.ReviewDeleteDto;
import fittoring.mentoring.business.service.dto.ReviewModifyDto;
import fittoring.mentoring.presentation.dto.MemberReviewGetResponse;
import fittoring.mentoring.presentation.dto.ReviewCreateResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        if (reservationRepository.existsByMentoringIdAndMemberId(mentoringId, reviewerId)) { // TODO: Member을 예약자 필드 이름으로 고치기
            return;
        }
        throw new ReservationNotFoundException(BusinessErrorMessage.REVIEWING_RESERVATION_NOT_FOUND.getMessage());
    }

    private void validateDuplicated(Long mentoringId, Long reviewerId) {
        if (reviewRepository.existsByMentoringIdAndReviewerId(mentoringId, reviewerId)) {
            throw new ReviewAlreadyExistsException(BusinessErrorMessage.DUPLICATED_REVIEW.getMessage());
        }
    }

    public List<MemberReviewGetResponse> findMemberReviews(MemberReviewGetDto dto) {
        List<Review> reviews = reviewRepository.findByReviewerId(dto.memberId());
        return reviews.stream()
            .map(review -> new MemberReviewGetResponse(
                review.getId(),
                review.getMentoring().getId(),
                review.getRating(),
                review.getContent()
            ))
            .toList();
    }

    @Transactional
    public void modifyReview(ReviewModifyDto reviewModifyDto) {
        Review review = reviewRepository.findById(reviewModifyDto.reviewId())
                .orElseThrow(() -> new ReviewNotFoundException(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage()));
        validateReviewer(review, reviewModifyDto.reviewerId());
        review.modify(reviewModifyDto.rating(), reviewModifyDto.content());
    }

    private void validateReviewer(Review review, Long reviewerId) {
        if (review.getReviewer().getId().equals(reviewerId)) {
            return;
        }
        throw new ReviewerNotSameException(BusinessErrorMessage.REVIEWER_NOT_SAME.getMessage());
    }

    @Transactional
    public void deleteReview(ReviewDeleteDto reviewDeleteDto) {
        Review review = reviewRepository.findById((reviewDeleteDto.reviewId()))
            .orElseThrow(() -> new ReviewNotFoundException(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage()));
        validateReviewer(review, reviewDeleteDto.reviewerId());
        reviewRepository.delete(review);
    }
}
