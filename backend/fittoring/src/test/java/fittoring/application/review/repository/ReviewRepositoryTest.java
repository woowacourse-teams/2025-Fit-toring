package fittoring.application.review.repository;

import fittoring.application.FixtureUtil;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringPaginationHelper;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.reservation.repository.RepositoryTestSupport;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import({MentoringPaginationHelper.class})
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
        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

        Reservation reservation1 = reservationRepository.save(
                FixtureUtil.getTestReservation(mentoring, mentee));
        Reservation reservation2 = reservationRepository.save(
                FixtureUtil.getTestReservation(mentoring, mentee));

        reviewRepository.save(FixtureUtil.getTestReview(reservation1, mentee));
        Review reviewToDelete = reviewRepository.save(FixtureUtil.getTestReview(reservation2, mentee));

        // when
        reviewRepository.delete(reviewToDelete);
        List<Review> actual = reviewRepository.findAll();

        // then
        assertThat(actual).hasSize(1);
    }

}
