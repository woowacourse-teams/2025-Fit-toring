package fittoring.integration.admin;

import fittoring.AbstractApiDocumentationTest;
import fittoring.admin.presentation.dto.AdminReviewInfoResponse;
import fittoring.admin.presentation.dto.AdminReviewResponse;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.mentoring.repository.MentoringStatisticsRepository;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.application.review.repository.ReviewRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.MentoringStatistics;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import fittoring.domain.model.Status;
import fittoring.domain.model.password.Password;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminReviewIntegrationTest extends AbstractApiDocumentationTest {

    private Member admin;
    private Member user;
    private String adminAccessToken;
    private String userAccessToken;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private MentoringStatisticsRepository mentoringStatisticsRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        admin = memberRepository.save(new Member(
                "adminId",
                "남",
                "관리자",
                new Phone("010-0000-0000"),
                Password.from("pw"),
                MemberRole.ADMIN
        ));
        adminAccessToken = jwtProvider.createAccessToken(admin.getId());
        user = memberRepository.save(new Member(
                "userId",
                "남",
                "멘티",
                new Phone("010-1111-1111"),
                Password.from("pw")
        ));
        userAccessToken = jwtProvider.createAccessToken(user.getId());
    }

    @DisplayName("관리자 리뷰 목록 조회")
    @Nested
    class ReviewsForAdmin {

        @DisplayName("관리자가 아닌 사용자가 리뷰 목록 조회롤 요청하면 403을 반환한다.")
        @Test
        void returnForbiddenReview() {
            // given
            Mentoring savedMentoring = mentoringRepository.save(new Mentoring(
                    admin,
                    1000,
                    1,
                    "content",
                    "introduction"
            ));

            // when
            // then
            RestAssured.given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", userAccessToken)
                    .when()
                    .get("/admin/mentorings/" + savedMentoring.getId() + "/reviews")
                    .then()
                    .log().all()
                    .statusCode(403);
        }

        @DisplayName("관리자가 존재하지 않는 멘토링의 리뷰 목록 조회롤 요청하면 404를 반환한다.")
        @Test
        void returnNotFoundReviewWithoutMentoring() {
            // given
            // when
            // then
            RestAssured.given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .when()
                    .get("/admin/mentorings/1/reviews")
                    .then()
                    .log().all()
                    .statusCode(404);
        }

        @DisplayName("관리자가 존재하는 멘토링의 리뷰 목록 조회롤 요청하면 200과 목록을 반환한다.")
        @Test
        void returnReviews() {
            // given
            Mentoring savedMentoring = mentoringRepository.save(new Mentoring(
                    admin,
                    1000,
                    1,
                    "content",
                    "introduction"
            ));
            mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(savedMentoring));
            Reservation savedReservation = reservationRepository.save(
                    new Reservation(
                            "content",
                            Status.COMPLETE,
                            savedMentoring,
                            user
                    ));
            mentoringStatisticsRepository.updateReservationCountPlus(savedMentoring.getId());
            Review savedReview = reviewRepository.save(new Review(5, "좋았어요", savedReservation, user));
            mentoringStatisticsRepository.updateReviewStatisticsPlus(savedMentoring.getId(), 5);

            // when
            // then
            var actual = RestAssured.given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .when()
                    .get("/admin/mentorings/" + savedMentoring.getId() + "/reviews")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<AdminReviewInfoResponse>() {
                    });
            var expected = new AdminReviewInfoResponse(
                    String.format("%.1f", savedReview.getRating() + 0.0),
                    1,
                    List.of(new AdminReviewResponse(
                            savedReview.getId(),
                            savedReview.getMenteeId(),
                            savedReview.getMenteeName(),
                            savedReview.getRating(),
                            savedReview.getContent(),
                            savedReview.getCreatedAt().truncatedTo(ChronoUnit.SECONDS)
                    )));
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(actual.ratingAverage())
                        .isEqualTo(expected.ratingAverage());
                softAssertions.assertThat(actual.ratingCount())
                        .isEqualTo(expected.ratingCount());
                softAssertions.assertThat(actual.reviewData())
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt")
                        .containsExactlyInAnyOrderElementsOf(expected.reviewData());
                softAssertions.assertThat(actual.reviewData().get(0).createdAt())
                        .isCloseTo(expected.reviewData().get(0).createdAt(), Assertions.within(1, ChronoUnit.SECONDS));
            });
        }
    }

    @DisplayName("관리자 리뷰 삭제")
    @Nested
    class ReviewsDeleteForAdmin {

        @DisplayName("관리자 권한 없이 리뷰 삭제를 요청하면 403을 반환한다.")
        @Test
        void failReviewDeleteWithoutAdmin() {
            // given
            // when
            // then
            RestAssured.given()
                    .log()
                    .all()
                    .contentType(ContentType.JSON)
                    .cookie("accessToken", userAccessToken)
                    .when()
                    .delete("/admin/reviews/1")
                    .then()
                    .log()
                    .all()
                    .statusCode(403);
        }

        @DisplayName("관리자가 존재하지 않는 리뷰 삭제를 요청하면 404을 반환한다.")
        @Test
        void failReviewDeleteWithoutReview() {
            // given
            // when
            // then
            RestAssured.given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .when()
                    .delete("/admin/reviews/1")
                    .then()
                    .log().all()
                    .statusCode(404);
        }

        @DisplayName("관리자가 존재하는 리뷰 삭제를 요청하면 204를 반환하고 리뷰를 삭제한다.")
        @Test
        void successReviewDelete() {
            // given
            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(admin,
                            1000,
                            1,
                            "content",
                            "introduction"
                    ));
            mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(savedMentoring));
            Reservation savedReservation = reservationRepository.save(
                    new Reservation(
                            "content",
                            Status.COMPLETE,
                            savedMentoring,
                            user
                    ));
            mentoringStatisticsRepository.updateReservationCountPlus(savedMentoring.getId());
            Review savedReview = reviewRepository.save(new Review(5, "좋았어요", savedReservation, user));
            mentoringStatisticsRepository.updateReviewStatisticsPlus(savedMentoring.getId(), 5);

            // when
            // then
            RestAssured.given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .when()
                    .delete("/admin/reviews/" + savedReview.getId())
                    .then()
                    .log().all()
                    .statusCode(204);
            AdminReviewInfoResponse afterActual = RestAssured.given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .when()
                    .get("/admin/mentorings/" + savedMentoring.getId() + "/reviews")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(AdminReviewInfoResponse.class);
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(afterActual.ratingAverage()).isEqualTo("0.0");
                softAssertions.assertThat(afterActual.ratingCount()).isZero();
                softAssertions.assertThat(afterActual.reviewData()).isEmpty();
            });
        }
    }
}
