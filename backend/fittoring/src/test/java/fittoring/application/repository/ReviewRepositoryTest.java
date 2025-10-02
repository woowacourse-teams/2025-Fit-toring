package fittoring.application.repository;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import fittoring.domain.model.Status;
import fittoring.domain.model.password.Password;
import fittoring.application.repository.helper.MentoringPaginationHelper;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({MentoringPaginationHelper.class})
class ReviewRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @DisplayName("리뷰를 조회할 때 상태 상태의 리뷰는 제외하고 조회한다.")
    @Test
    void findReviews() {
        //given
        Member mentee = memberRepository.save(new Member(
                "loginId",
                "MALE",
                "name",
                new Phone("010-1234-5678"),
                Password.from("password")
        ));
        Member mentor = memberRepository.save(new Member(
                "mentorId",
                "MALE",
                "김트레이너",
                new Phone("010-1111-2222"),
                Password.from("password")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "한 줄 소개",
                "긴 글 소개",
                "가상의오픈채팅링크"
        ));
        Reservation reservation = reservationRepository.save(new Reservation(
                "예약합니다.",
                Status.COMPLETE,
                mentoring,
                mentee
        ));
        Reservation reservation2 = reservationRepository.save(new Reservation(
                "두번째 예약합니다.",
                Status.COMPLETE,
                mentoring,
                mentee
        ));
        reviewRepository.save(new Review(
                5,
                "최고의 멘토링이었습니다.",
                reservation,
                mentee
        ));
        Review review2 = reviewRepository.save(new Review(
                3,
                "최고의 멘토링이었습니다.",
                reservation2,
                mentee
        ));

        reviewRepository.delete(review2);

        //when
        List<Review> actual = reviewRepository.findAll();

        //then
        assertThat(actual).hasSize(1);
    }
}
