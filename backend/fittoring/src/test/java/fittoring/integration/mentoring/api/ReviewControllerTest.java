package fittoring.integration.mentoring.api;

import static org.hamcrest.Matchers.equalTo;

import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.service.JwtProvider;
import fittoring.mentoring.presentation.dto.ReviewCreateRequest;
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
    private MentoringRepository mentoringRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JwtProvider jwtProvider;

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
        Password password = Password.from("password");
        Member mentor = memberRepository.save(new Member(
            "mentor",
            "MALE",
            "김트레이너",
            new Phone("010-2222-3333"),
            password
        ));
        Member mentee = memberRepository.save(new Member(
            "loginId",
            "MALE",
            "name",
            new Phone("010-1234-5678"),
            password
        ));
        String accessToken = jwtProvider.createAccessToken(mentee.getId());
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            mentor,
            5000,
            5,
            "content",
            "introduction"
        ));
        reservationRepository.save(new Reservation(
            "예약 신청합니다.",
            mentoring,
            mentee
        ));
        int rating = 4;
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
            .cookie("accessToken", accessToken)
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

    @DisplayName("존재하지 않는 멘토링에 리뷰 작성을 요청하면 404 Not Found를 반환한다")
    @Test
    void createReviewFail1() {
        // given
        Password password = Password.from("password");
        Member mentor = memberRepository.save(new Member(
            "mentor",
            "MALE",
            "김트레이너",
            new Phone("010-2222-3333"),
            password
        ));
        Member mentee = memberRepository.save(new Member(
            "loginId",
            "MALE",
            "name",
            new Phone("010-1234-5678"),
            password
        ));
        String accessToken = jwtProvider.createAccessToken(mentee.getId());
        int rating = 4;
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
            .cookie("accessToken", accessToken)
            .body(requestBody)
            .when()
            .post("/mentorings/999/review")
            .then().log().all()
            .statusCode(404);
    }

    @DisplayName("존재하지 않는 멤버가 리뷰 작성을 요청하면 404 Not Found를 반환한다")
    @Test
    void createReviewFail2() {
        // given
        Member mentor = memberRepository.save(new Member(
            "mentor",
            "MALE",
            "김트레이너",
            new Phone("010-2222-3333"),
            Password.from("password")
        ));
        Member mentee = memberRepository.save(new Member(
            "loginId",
            "MALE",
            "name",
            new Phone("010-1234-5678"),
            Password.from("password")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            mentor,
            5000,
            5,
            "content",
            "introduction"
        ));
        int rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
            rating,
            content
        );
        String accessTokenWithUnexistMemberId = jwtProvider.createAccessToken(999L);

        // when
        // then
        RestAssured
            .given()
            .log().all().contentType(ContentType.JSON)
            .cookie("accessToken", accessTokenWithUnexistMemberId)
            .body(requestBody)
            .when()
            .post("/mentorings/" + mentoring.getId() + "/review")
            .then().log().all()
            .statusCode(404);
    }

    @DisplayName("신청하지 않았던 멘토링에 리뷰 작성을 요청하면 404 Not Found를 반환한다")
    @Test
    void createReviewFail3() {
        // given
        Password password = Password.from("password");
        Member mentor = memberRepository.save(new Member(
            "mentor",
            "MALE",
            "김트레이너",
            new Phone("010-2222-3333"),
            password
        ));
        Member mentee = memberRepository.save(new Member(
            "loginId",
            "MALE",
            "name",
            new Phone("010-1234-5678"),
            password
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            mentor,
            5000,
            5,
            "content",
            "introduction"
        ));
        int rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
            rating,
            content
        );
        Member anotherMember = memberRepository.save(new Member(
            "loginId2",
            "MALE",
            "name2",
            new Phone("010-1234-5679"),
            Password.from("password")
        ));
        String accessTokenWithAnotherMember = jwtProvider.createAccessToken(anotherMember.getId());

        // when
        // then
        RestAssured
            .given()
            .log().all().contentType(ContentType.JSON)
            .cookie("accessToken", accessTokenWithAnotherMember)
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
        Password password = Password.from("password");
        Member mentor = memberRepository.save(new Member(
            "mentor",
            "MALE",
            "김트레이너",
            new Phone("010-2222-3333"),
            password
        ));
        Member mentee = memberRepository.save(new Member(
            "loginId",
            "MALE",
            "name",
            new Phone("010-1234-5678"),
            password
        ));
        String accessToken = jwtProvider.createAccessToken(mentee.getId());
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            mentor,
            5000,
            5,
            "content",
            "introduction"
        ));
        reservationRepository.save(new Reservation(
            "예약 신청합니다.",
            mentoring,
            mentee
        ));
        int rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
            rating,
            content
        );
        RestAssured
            .given()
            .log().all().contentType(ContentType.JSON)
            .cookie("accessToken", accessToken)
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
            .cookie("accessToken", accessToken)
            .body(requestBody)
            .when()
            .post("/mentorings/" + mentoring.getId() + "/review")
            .then().log().all()
            .statusCode(400);
    }
}
