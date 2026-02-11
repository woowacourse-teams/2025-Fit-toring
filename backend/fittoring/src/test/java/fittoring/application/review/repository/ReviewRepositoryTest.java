package fittoring.application.review.repository;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.RepositoryTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReviewRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @DisplayName("리뷰를 조회할 때 삭제 상태의 리뷰는 제외하고 조회한다.")
    @Test
    void findReviews() {
        // given
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));

        Reservation reservation1 = reservationRepository.save(
                FixtureUtil.testPendingReservation(mentoring, mentee));
        Reservation reservation2 = reservationRepository.save(
                FixtureUtil.testPendingReservation(mentoring, mentee));

        reviewRepository.save(FixtureUtil.testReview(reservation1, mentee));
        Review reviewToDelete = reviewRepository.save(FixtureUtil.testReview(reservation2, mentee));

        // when
        reviewRepository.delete(reviewToDelete);
        List<Review> actual = reviewRepository.findAll();

        // then
        assertThat(actual).hasSize(1);
    }

}
