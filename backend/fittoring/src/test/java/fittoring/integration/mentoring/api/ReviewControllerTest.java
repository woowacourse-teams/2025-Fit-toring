package fittoring.integration.mentoring.api;

import static org.hamcrest.Matchers.equalTo;

import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Review;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.repository.ReviewRepository;
import fittoring.mentoring.business.service.dto.ReviewCreateDto;
import fittoring.mentoring.presentation.dto.ReviewCreateRequest;
import fittoring.mentoring.presentation.dto.ReviewCreateResponse;
import fittoring.mentoring.presentation.dto.ReviewModifyRequest;
import fittoring.util.DbCleaner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ReviewControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        dbCleaner.clean();
    }

    @DisplayName("리뷰 작성에 성공하면 201 Created 상태코드와 별점, 리뷰 내용을 반환한다")
    @Test
    void createReview() {
        // given
        Member reviewer = memberRepository.save(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        reservationRepository.save(new Reservation()); // TODO: 예약 추가하기
        byte rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
            rating,
            content
        );

        // when
        // then
        RestAssured
            .given()
            .log().all().contentType(ContentType.JSON)
            // TODO: 작성자 정보 넣기
            .body(requestBody)
            .when()
            .post("/mentorings/" + mentoring.getId() + "/review")
            .then().log().all()
            .statusCode(201)
            .body(
                "rating", equalTo(rating),
                "content", equalTo(content)
            );
    }

    @DisplayName("존재하지 않는 멘토링에 리뷰 작성을 요청하면 400 Bad Request를 반환한다")
    @Test
    void createReviewFail1() {
        // given
        byte rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
            rating,
            content
        );

        // when
        // then
        RestAssured
            .given()
            .log().all().contentType(ContentType.JSON)
            // TODO: 작성자 정보 넣기
            .body(requestBody)
            .when()
            .post("/mentorings/999/review")
            .then().log().all()
            .statusCode(400);
    }

    @DisplayName("존재하지 않는 멤버가 리뷰 작성을 요청하면 400 Bad Request를 반환한다")
    @Test
    void createReviewFail2() {
        // given
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        byte rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
            rating,
            content
        );

        // when
        // then
        RestAssured
            .given()
            .log().all().contentType(ContentType.JSON)
            // TODO: 작성자 정보 넣기
            .body(requestBody)
            .when()
            .post("/mentorings/" + mentoring.getId() + "/review")
            .then().log().all()
            .statusCode(400);
    }

    @DisplayName("신청하지 않았던 멘토링에 리뷰 작성을 요청하면 404 Not Found를 반환한다")
    @Test
    void createReviewFail3() {
        // given
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        byte rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
            rating,
            content
        );

        // when
        // then
        RestAssured
            .given()
            .log().all().contentType(ContentType.JSON)
            // TODO: 작성자 정보 넣기
            .body(requestBody)
            .when()
            .post("/mentorings/" + mentoring.getId() + "/review")
            .then().log().all()
            .statusCode(404);
    }

    @DisplayName("이미 리뷰를 작성했던 멘토링에 중복으로 리뷰 작성을 요청하면 400 Bad Request를 반환한다")
    @Test
    void createReviewFail4() {
        // given
        Member reviewer = memberRepository.save(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        reservationRepository.save(new Reservation()); // TODO: 예약 추가하기
        byte rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
            rating,
            content
        );
        RestAssured
            .given()
            .log().all().contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/mentorings/" + mentoring.getId() + "/review")
            .then().log().all()
            .statusCode(201);

        // when
        // then
        RestAssured
            .given()
            .log().all().contentType(ContentType.JSON)
            // TODO: 작성자 정보 넣기
            .body(requestBody)
            .when()
            .post("/mentorings/" + mentoring.getId() + "/review")
            .then().log().all()
            .statusCode(400);
    }

    @DisplayName("리뷰 수정에 성공하면 200 OK를 반환한다")
    @Test
    void modifyReview() {
        // given
        Member reviewer = memberRepository.save(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        reservationRepository.save(new Reservation()); // TODO: 예약 추가하기
        Review review = reviewRepository.save(new Review(
            (byte) 4,
            "전반적으로 좋았습니다.",
            mentoring,
            reviewer
        ));
        ReviewModifyRequest requestBody = new ReviewModifyRequest(
            (byte) 2,
            "생각해 보니 비용이 너무 비쌌던 것 같아요"
        );

        // when
        // then
        RestAssured
            .given().log().all().contentType(ContentType.JSON)
            // TODO: 작성자 정보 넣기
            .body(requestBody)
            .when()
            .patch("/reviews/" + review.getId())
            .then().log().all()
            .statusCode(200);
    }

    @DisplayName("본인이 작성하지 않은 리뷰를 수정하려고 하면 400 Bad Request를 반환한다")
    @Test
    void modifyReviewFail() {
        // given
        Member reviewer = memberRepository.save(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        reservationRepository.save(new Reservation()); // TODO: 예약 추가하기
        Review review = reviewRepository.save(new Review(
            (byte) 4,
            "전반적으로 좋았습니다.",
            mentoring,
            reviewer
        ));
        Member invalidMember = memberRepository.save(new Member(
            "loginId2",
            "남",
            "name2",
            "010-1234-5679",
            "password"
        ));
        ReviewModifyRequest requestBody = new ReviewModifyRequest(
            (byte) 2,
            "생각해 보니 비용이 너무 비쌌던 것 같아요"
        );

        // when
        // then
        RestAssured
            .given().log().all().contentType(ContentType.JSON)
            // TODO: 작성자 정보 넣기
            .body(requestBody)
            .when()
            .patch("/reviews/" + review.getId())
            .then().log().all()
            .statusCode(400);
    }

    @DisplayName("리뷰 삭제에 성공하면 204 NO CONTENT를 반환한다")
    @Test
    void deleteReview() {
        // given
        Member reviewer = memberRepository.save(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        reservationRepository.save(new Reservation()); // TODO: 예약 추가하기
        Review review = reviewRepository.save(new Review(
            (byte) 4,
            "전반적으로 좋았습니다.",
            mentoring,
            reviewer
        ));

        // when
        // then
        RestAssured
            .given().log().all().contentType(ContentType.JSON)
            // TODO: 작성자 정보 넣기
            .when()
            .delete("/reviews/" + review.getId())
            .then().log().all()
            .statusCode(204);
    }

    @DisplayName("존재하지 않는 리뷰 삭제 요청 시 404 NOT FOUND를 반환한다")
    @Test
    void deleteReviewFail1() {
        // given
        Member reviewer = memberRepository.save(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));

        // when
        // then
        RestAssured
            .given().log().all().contentType(ContentType.JSON)
            // TODO: 작성자 정보 넣기
            .when()
            .delete("/reviews/999")
            .then().log().all()
            .statusCode(404);
    }

    @DisplayName("본인이 작성하지 않은 리뷰를 삭제하려고 하면 400 Bad Request를 반환한다")
    @Test
    void deleteReviewFail2() {
        // given
        Member reviewer = memberRepository.save(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        reservationRepository.save(new Reservation()); // TODO: 예약 추가하기
        Review review = reviewRepository.save(new Review(
            (byte) 4,
            "전반적으로 좋았습니다.",
            mentoring,
            reviewer
        ));
        Member invalidMember = memberRepository.save(new Member(
            "loginId2",
            "남",
            "name2",
            "010-1234-5679",
            "password"
        ));

        // when
        // then
        RestAssured
            .given().log().all().contentType(ContentType.JSON)
            // TODO: 작성자 정보 넣기
            .when()
            .delete("/reviews/" + review.getId())
            .then().log().all()
            .statusCode(400);
    }
}
