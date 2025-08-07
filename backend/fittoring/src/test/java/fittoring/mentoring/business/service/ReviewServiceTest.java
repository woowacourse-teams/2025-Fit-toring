package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.config.JpaConfiguration;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MemberNotFoundException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.ReservationNotFoundException;
import fittoring.mentoring.business.exception.ReviewAlreadyExistsException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Review;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.service.dto.MemberReviewGetDto;
import fittoring.mentoring.business.service.dto.ReviewCreateDto;
import fittoring.mentoring.presentation.dto.MemberReviewGetResponse;
import fittoring.mentoring.presentation.dto.ReviewCreateResponse;
import fittoring.util.DbCleaner;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({DbCleaner.class, ReviewService.class, JpaConfiguration.class})
@DataJpaTest
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("리뷰 작성을 성공하면 별점과 리뷰 내용을 반환한다")
    @Test
    void createReservation() {
        // given
        Password password = Password.from("password");
        Member mentor = entityManager.persist(new Member(
            "mentor",
            "MALE",
            "김트레이너",
            new Phone("010-2222-3333"),
            password
        ));
        Member mentee = entityManager.persist(new Member(
            "loginId",
            "MALE",
            "name",
            new Phone("010-1234-5678"),
            password
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
            mentor,
            5000,
            5,
            "content",
            "introduction"
        ));
        Reservation reservation = entityManager.persist(new Reservation(
            "예약 신청합니다.",
            mentoring,
            mentee
        ));
        int rating = 5;
        String content = "최고의 멘토링이었습니다.";
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(
            mentee.getId(),
            reservation.getId(),
            rating,
            content
        );

        // when
        ReviewCreateResponse reviewCreateResponse = reviewService.createReview(reviewCreateDto);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(reviewCreateResponse.rating()).isEqualTo(rating);
            softAssertions.assertThat(reviewCreateResponse.content()).isEqualTo(content);
        });
    }

    @DisplayName("존재하지 않는 멤버의 요청이라면 예외가 발생한다.")
    @Test
    void createReservationFail1() {
        // given
        Member mentor = entityManager.persist(new Member(
            "mentor",
            "MALE",
            "김트레이너",
            new Phone("010-2222-3333"),
            Password.from("password")
        ));
        Member mentee = entityManager.persist(new Member(
            "loginId",
            "MALE",
            "name",
            new Phone("010-1234-5678"),
            Password.from("password")
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
            mentor,
            5000,
            5,
            "content",
            "introduction"
        ));
        entityManager.persist(new Reservation(
            "예약 신청합니다.",
            mentoring,
            mentee
        ));
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(
            999L,
            1L,
            5,
            "최고의 멘토링이었습니다."
        );

        // when
        // then
        assertThatThrownBy(() -> reviewService.createReview(reviewCreateDto))
            .isInstanceOf(MemberNotFoundException.class)
            .hasMessage(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("신청하지 않았던 멘토링에 리뷰 작성을 요청하면 예외가 발생한다")
    @Test
    void createReservationFail2() {
        // given
        Password password = Password.from("password");
        Member mentor = entityManager.persist(new Member(
            "mentor",
            "MALE",
            "김트레이너",
            new Phone("010-2222-3333"),
            password
        ));
        Member mentee = entityManager.persist(new Member(
            "loginId",
            "MALE",
            "name",
            new Phone("010-1234-5678"),
            password
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
            mentor,
            5000,
            5,
            "content",
            "introduction"
        ));
        Reservation reservation = entityManager.persist(new Reservation(
            "예약 신청합니다.",
            mentoring,
            mentee
        ));
        Member anotherMember = entityManager.persist(new Member(
            "anotherMember",
            "MALE",
            "김멘티",
            new Phone("010-2222-3334"),
            password
        ));
        int rating = 5;
        String content = "최고의 멘토링이었습니다.";
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(
            anotherMember.getId(),
            reservation.getId(),
            rating,
            content
        );

        // when
        // then
        assertThatThrownBy(() -> reviewService.createReview(reviewCreateDto))
            .isInstanceOf(ReservationNotFoundException.class)
            .hasMessage(BusinessErrorMessage.REVIEWING_RESERVATION_NOT_FOUND.getMessage());
    }

    @DisplayName("이미 리뷰를 작성했던 멘토링에 중복으로 리뷰 작성을 요청하면 예외가 발생한다")
    @Test
    void createReservationFail3() {
        // given
        Password password = Password.from("password");
        Member mentor = entityManager.persist(new Member(
            "mentor",
            "MALE",
            "김트레이너",
            new Phone("010-2222-3333"),
            password
        ));
        Member mentee = entityManager.persist(new Member(
            "loginId",
            "MALE",
            "name",
            new Phone("010-1234-5678"),
            password
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
            mentor,
            5000,
            5,
            "content",
            "introduction"
        ));
        Reservation reservation = entityManager.persist(new Reservation(
            "예약 신청합니다.",
            mentoring,
            mentee
        ));
        int rating = 5;
        String content = "최고의 멘토링이었습니다.";
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(
            mentee.getId(),
            reservation.getId(),
            rating,
            content
        );
        reviewService.createReview(reviewCreateDto);

        // when
        // then
        assertThatThrownBy(() -> reviewService.createReview(reviewCreateDto))
            .isInstanceOf(ReviewAlreadyExistsException.class)
            .hasMessage(BusinessErrorMessage.DUPLICATED_REVIEW.getMessage());
    }

    @DisplayName("특정 멤버의 리뷰를 모두 조회 성공 시 리뷰 정보를 반환한다")
    @Test
    void findMemberReviews() {
        // given
        Member mentee = entityManager.persist(new Member(
            "loginId",
            "MALE",
            "name",
            new Phone("010-1234-5678"),
            Password.from("password")
        ));
        Member mentor1 = entityManager.persist(new Member(
            "mentor1Id",
            "MALE",
            "김트레이너",
            new Phone("010-1111-2222"),
            Password.from("password")
        ));
        Member mentor2 = entityManager.persist(new Member(
            "mentor2Id",
            "MALE",
            "박멘토",
            new Phone("010-2222-3333"),
            Password.from("password")
        ));
        Mentoring mentoring1 = entityManager.persist(new Mentoring(
            mentor1,
            5000,
            5,
            "한 줄 소개",
            "긴 글 소개"
        ));
        Mentoring mentoring2 = entityManager.persist(new Mentoring(
            mentor2,
            5000,
            5,
            "한 줄 소개",
            "긴 글 소개"
        ));
        Reservation reservation1 = entityManager.persist(new Reservation(
            "예약합니다.",
            mentoring1,
            mentee
        ));
        Reservation reservation2 = entityManager.persist(new Reservation(
            "예약합니다.",
            mentoring2,
            mentee
        ));
        Review review1 = entityManager.persist(new Review(
            5,
            "최고의 멘토링이었습니다.",
            reservation1,
            mentee
        ));
        Review review2 = entityManager.persist(new Review(
            5,
            "최고의 멘토링이었습니다.",
            reservation2,
            mentee
        ));
        MemberReviewGetDto memberReviewGetDto = new MemberReviewGetDto(mentee.getId());
        List<MemberReviewGetResponse> expected = List.of(
            new MemberReviewGetResponse(
                review1.getId(),
                review1.getCreatedAt().toLocalDate(),
                review1.getRating(),
                review1.getContent()
            ),
            new MemberReviewGetResponse(
                review2.getId(),
                review2.getCreatedAt().toLocalDate(),
                review2.getRating(),
                review2.getContent()
            )
        );

        // when
        List<MemberReviewGetResponse> memberReviewGetResponses =
            reviewService.findMemberReviews(memberReviewGetDto);

        // then
        assertThat(memberReviewGetResponses).containsExactlyInAnyOrderElementsOf(expected);
    }
}
