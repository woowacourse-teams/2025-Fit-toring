package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MemberNotFoundException;
import fittoring.mentoring.business.exception.ReservationNotFoundException;
import fittoring.mentoring.business.exception.ReviewAlreadyExistsException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Review;
import fittoring.mentoring.business.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    @Transactional
    public ReviewCreateResponse createReview(ReviewCreateDto dto) {
        Review review = createNewReview(dto.reservationId(), dto.menteeId(), dto.rating(), dto.content());
        Review savedReview = reviewRepository.save(review);
        return ReviewCreateResponse.of(savedReview);
    }

    private Review createNewReview(Long reservationId, Long menteeId, int rating, String content) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(BusinessErrorMessage.RESERVATION_NOT_FOUND.getMessage()));
        Member mentee = memberRepository.findById(menteeId)
            .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        validateReservationOwnership(reservation, menteeId);
        validateReviewNotDuplicated(reservation);
        return new Review(rating, content, reservation, mentee);
    }

    private void validateReservationOwnership(Reservation reservation, Long menteeId) {
        if (reservation.getMentee().getId().equals(menteeId)) {
            return;
        }
        throw new ReservationNotFoundException(BusinessErrorMessage.REVIEWING_RESERVATION_NOT_FOUND.getMessage());
    }

    private void validateReviewNotDuplicated(Reservation reservation) {
        if (reviewRepository.existsByReservationId(reservation.getId())) {
            throw new ReviewAlreadyExistsException(BusinessErrorMessage.DUPLICATED_REVIEW.getMessage());
        }
    }
}
