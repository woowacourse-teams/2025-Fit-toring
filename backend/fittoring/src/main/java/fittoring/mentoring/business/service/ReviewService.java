package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.ForbiddenException;
import fittoring.mentoring.business.exception.MemberNotFoundException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.ReservationNotCompletedException;
import fittoring.mentoring.business.exception.ReservationNotFoundException;
import fittoring.mentoring.business.exception.ReviewAlreadyExistsException;
import fittoring.mentoring.business.exception.ReviewNotFoundException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Review;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.repository.ReviewRepository;
import fittoring.mentoring.business.service.dto.RatingStatsDto;
import fittoring.mentoring.business.service.dto.ReviewCreateDto;
import fittoring.mentoring.business.service.dto.ReviewDeleteDto;
import fittoring.mentoring.business.service.dto.ReviewModifyDto;
import fittoring.mentoring.presentation.dto.AdminReviewInfoResponse;
import fittoring.mentoring.presentation.dto.AdminReviewResponse;
import fittoring.mentoring.presentation.dto.MemberReviewGetResponse;
import fittoring.mentoring.presentation.dto.ReviewCreateResponse;
import fittoring.mentoring.presentation.dto.ReviewGetResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final MentoringRepository mentoringRepository;

    @Transactional
    public ReviewCreateResponse createReview(ReviewCreateDto dto) {
        Review review = createNewReview(dto.reservationId(), dto.menteeId(), dto.rating(), dto.content());
        Review savedReview = reviewRepository.save(review);
        Mentoring mentoring = mentoringRepository.findByReviewId(savedReview.getId())
                .orElseThrow(() -> new ReviewNotFoundException(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage()));
        return new ReviewCreateResponse(
                mentoring.getId(),
                savedReview.getRating(),
                savedReview.getContent()
        );
    }

    private Review createNewReview(Long reservationId, Long menteeId, int rating, String content) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(
                        BusinessErrorMessage.RESERVATION_NOT_FOUND.getMessage()));
        validateReservationIsCompleted(reservation);
        Member mentee = memberRepository.findById(menteeId)
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        validateReservationOwnership(reservation, menteeId);
        validateReviewNotDuplicated(reservation, menteeId);
        return new Review(rating, content, reservation, mentee);
    }

    private void validateReservationIsCompleted(Reservation reservation) {
        if (reservation.isComplete()) {
            return;
        }
        throw new ReservationNotCompletedException(BusinessErrorMessage.RESERVATION_NOT_COMPLETED.getMessage());
    }

    private void validateReservationOwnership(Reservation reservation, Long menteeId) {
        if (reservation.isCreatedByMember(menteeId)) {
            return;
        }
        throw new ReservationNotFoundException(BusinessErrorMessage.REVIEWING_RESERVATION_NOT_FOUND.getMessage());
    }

    private void validateReviewNotDuplicated(Reservation reservation, Long menteeId) {
        if (reviewRepository.existsByReservationIdAndMentee_Id(reservation.getId(), menteeId)) {
            throw new ReviewAlreadyExistsException(BusinessErrorMessage.DUPLICATED_REVIEW.getMessage());
        }
    }

    public List<MemberReviewGetResponse> findMemberReviews(Long memberId) {
        return reviewRepository.findMemberReviews(memberId);
    }

    @Transactional
    public List<ReviewGetResponse> findMentoringReviews(Long mentoringId) {
        List<Review> reviews = reviewRepository.findAllByReservationMentoringIdOrderByCreatedAtDesc(mentoringId);
        return reviews.stream()
                .map(review -> new ReviewGetResponse(
                        review.getId(),
                        review.getMenteeName(),
                        review.getCreatedAt().toLocalDate(),
                        review.getRating(),
                        review.getContent()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminReviewInfoResponse findAllByMentoringForAdmin(Long memberId, Long mentoringId) {
        validateAdmin(memberId);
        validateMentoringExists(mentoringId);
        List<AdminReviewResponse> reviewResponses = findReviewResponsesForAdmin(mentoringId);
        RatingStatsDto reviewInfo = reviewRepository.findRatingStatsByMentoringId(mentoringId)
                .orElse(RatingStatsDto.defaultOf(mentoringId));
        return AdminReviewInfoResponse.of(reviewResponses, reviewInfo);
    }

    private void validateAdmin(Long memberId) {
        Member admin = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        if (admin.isNotAdmin()) {
            throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
        }
    }

    private void validateMentoringExists(Long mentoringId) {
        if (!mentoringRepository.existsById(mentoringId)) {
            throw new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage());
        }
    }

    private List<AdminReviewResponse> findReviewResponsesForAdmin(Long mentoringId) {
        List<Review> reviews = reviewRepository.findAllByReservationMentoringIdOrderByCreatedAtDesc(mentoringId);
        return reviews.stream()
                .map(review -> AdminReviewResponse.of(review, review.getMentee()))
                .toList();
    }

    @Transactional
    public void modifyReview(ReviewModifyDto reviewModifyDto) {
        Review review = reviewRepository.findById(reviewModifyDto.reviewId())
                .orElseThrow(() -> new ReviewNotFoundException(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage()));
        validateReviewOwner(review, reviewModifyDto.menteeId());
        review.modify(reviewModifyDto.rating(), reviewModifyDto.content());
    }

    private void validateReviewOwner(Review review, Long menteeId) {
        if (review.getMenteeId().equals(menteeId)) {
            return;
        }
        throw new ForbiddenException(BusinessErrorMessage.NOT_REVIEW_OWNER.getMessage());
    }

    @Transactional
    public void deleteReview(ReviewDeleteDto reviewDeleteDto) {
        Review review = reviewRepository.findById((reviewDeleteDto.reviewId()))
                .orElseThrow(() -> new ReviewNotFoundException(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage()));
        validateReviewOwner(review, reviewDeleteDto.menteeId());
        reviewRepository.delete(review);
    }

    @Transactional
    public void deleteForAdmin(Long memberId, Long reviewId) {
        validateAdmin(memberId);
        validateReviewExists(reviewId);
        reviewRepository.deleteById(reviewId);
    }

    private void validateReviewExists(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage());
        }
    }
}
