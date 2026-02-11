package fittoring.application.review.service;

import fittoring.admin.presentation.dto.AdminReviewInfoResponse;
import fittoring.admin.presentation.dto.AdminReviewResponse;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.MentoringNotFoundException;
import fittoring.application.exception.ReservationNotCompletedException;
import fittoring.application.exception.ReservationNotFoundException;
import fittoring.application.exception.ReviewAlreadyExistsException;
import fittoring.application.exception.ReviewNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.mentoring.repository.MentoringStatisticsRepository;
import fittoring.application.mentoring.service.dto.RatingStatsDto;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.application.review.presentation.dto.response.MemberReviewGetResponse;
import fittoring.application.review.presentation.dto.response.ReviewCreateResponse;
import fittoring.application.review.presentation.dto.response.ReviewGetResponse;
import fittoring.application.review.repository.ReviewRepository;
import fittoring.application.review.service.dto.ReviewCreateDto;
import fittoring.application.review.service.dto.ReviewDeleteDto;
import fittoring.application.review.service.dto.ReviewModifyDto;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.MentoringStatistics;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
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
    private final MentoringStatisticsRepository mentoringStatisticsRepository;

    @Transactional
    public ReviewCreateResponse createReview(ReviewCreateDto dto) {
        Review review = createNewReview(dto.reservationId(), dto.menteeId(), dto.rating(), dto.content());
        Review savedReview = reviewRepository.save(review);
        Mentoring mentoring = mentoringRepository.findByReviewId(savedReview.getId())
                .orElseThrow(() -> new ReviewNotFoundException(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage()));
        mentoringStatisticsRepository.updateReviewStatisticsPlus(mentoring.getId(), savedReview.getRating());
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
        List<Review> reviews = reviewRepository.findAllByMentee_Id(memberId);
        return reviews.stream()
                .map(review -> new MemberReviewGetResponse(
                        review.getId(),
                        review.getCreatedAt().toLocalDate(),
                        review.getRating(),
                        review.getContent()
                ))
                .toList();
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
    public AdminReviewInfoResponse findAllByMentoringForAdmin(Long mentoringId) {
        validateMentoringExists(mentoringId);
        List<AdminReviewResponse> reviewResponses = findReviewResponsesForAdmin(mentoringId);
        MentoringStatistics mentoringStatistics = mentoringStatisticsRepository.findById(mentoringId).get();
        RatingStatsDto reviewInfo = new RatingStatsDto(
                mentoringId,
                mentoringStatistics.getAverageRating(),
                mentoringStatistics.getReviewCount()
        );
        return AdminReviewInfoResponse.of(reviewResponses, reviewInfo);
    }

    private void validateMentoringExists(Long mentoringId) {
        if (mentoringRepository.existsById(mentoringId) && mentoringStatisticsRepository.existsById(mentoringId)) {
            return;
        }
        throw new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage());
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
        Mentoring mentoring = review.getReservation().getMentoring();
        mentoringStatisticsRepository.updateReviewStatisticsMinus(mentoring.getId(), review.getRating());
        reviewRepository.delete(review);
    }

    @Transactional
    public void deleteForAdmin(Long reviewId) {
        Review review = reviewRepository.findById((reviewId))
                .orElseThrow(() -> new ReviewNotFoundException(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage()));
        Mentoring mentoring = review.getReservation().getMentoring();
        mentoringStatisticsRepository.updateReviewStatisticsMinus(mentoring.getId(), review.getRating());
        reviewRepository.delete(review);
    }
}
