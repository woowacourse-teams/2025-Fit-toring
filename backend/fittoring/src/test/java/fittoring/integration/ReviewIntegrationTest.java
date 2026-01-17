package fittoring.integration;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import fittoring.AbstractApiDocumentationTest;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.application.review.presentation.dto.request.ReviewCreateRequest;
import fittoring.application.review.presentation.dto.request.ReviewModifyRequest;
import fittoring.application.review.presentation.dto.response.MemberReviewGetResponse;
import fittoring.application.review.presentation.dto.response.ReviewCreateResponse;
import fittoring.application.review.presentation.dto.response.ReviewGetResponse;
import fittoring.application.review.repository.ReviewRepository;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import fittoring.domain.model.Status;
import fittoring.domain.model.password.Password;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;

class ReviewIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("리뷰 작성에 성공하면 201 Created 상태코드와 별점, 리뷰 내용을 반환한다")
    @Test
    void createReview() {
        // given
        Password password = Password.from("password");
        Member mentor = memberRepository.save(new Member(
                "mentor",
                Gender.MALE,
                "김트레이너",
                new Phone("010-2222-3333"),
                password
        ));
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
                "name",
                new Phone("010-1234-5678"),
                password
        ));
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "content",
                "introduction"
        ));
        Reservation reservation = reservationRepository.save(
                new Reservation(
                        "예약 신청합니다.",
                        Status.COMPLETE,
                        mentoring,
                        mentee
                )
        );
        int rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
                reservation.getId(),
                rating,
                content
        );

        // when
        ReviewCreateResponse response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/post-reviews-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .summary("리뷰 작성")
                                .description("완료된 멘토링에 대해 리뷰를 작성합니다. 성공 시 201 Created, 실패 시 400 Bad Request 또는 404 Not Found를 반환합니다.")
                                .requestSchema(Schema.schema("ReviewCreateRequest"))
                                .requestFields(
                                        fieldWithPath("reservationId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("예약 ID"),
                                        fieldWithPath("rating")
                                                .type(JsonFieldType.NUMBER)
                                                .description("평점 (1~5)"),
                                        fieldWithPath("content")
                                                .type(JsonFieldType.STRING)
                                                .description("리뷰 내용")
                                                .optional()
                                )
                                .responseSchema(Schema.schema("ReviewCreateResponse"))
                                .responseFields(
                                        fieldWithPath("mentoringId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("리뷰가 작성된 멘토링 ID"),
                                        fieldWithPath("rating")
                                                .type(JsonFieldType.NUMBER)
                                                .description("리뷰 평점 (1~5)"),
                                        fieldWithPath("content")
                                                .type(JsonFieldType.STRING)
                                                .description("리뷰 내용")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(requestBody)
                .when()
                .post("/reviews")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(ReviewCreateResponse.class);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.rating()).isEqualTo(rating);
            softly.assertThat(response.content()).isEqualTo(content);
        });
    }

    @DisplayName("존재하지 않는 멤버가 리뷰 작성을 요청하면 404 Not Found를 반환한다")
    @Test
    void createReviewFail1() {
        // given
        Member mentor = memberRepository.save(new Member(
                "mentor",
                Gender.MALE,
                "김트레이너",
                new Phone("010-2222-3333"),
                Password.from("password")
        ));
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
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
        Reservation reservation = reservationRepository.save(
                new Reservation(
                        "예약 신청합니다.",
                        Status.COMPLETE,
                        mentoring,
                        mentee
                )
        );
        int rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
                reservation.getId(),
                rating,
                content
        );
        String accessTokenWithUnexistMemberId = jwtProvider.createAccessToken(999L, MemberRole.MENTEE);

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/post-reviews-fail-member-not-found",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .requestSchema(Schema.schema("ReviewCreateRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessTokenWithUnexistMemberId)
                .body(requestBody)
                .when()
                .post("/reviews")
                .then().log().all()
                .statusCode(404);
    }

    @DisplayName("신청하지 않았던 멘토링에 리뷰 작성을 요청하면 404 Not Found를 반환한다")
    @Test
    void createReviewFail2() {
        // given
        Password password = Password.from("password");
        Member mentor = memberRepository.save(new Member(
                "mentor",
                Gender.MALE,
                "김트레이너",
                new Phone("010-2222-3333"),
                password
        ));
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
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
        Reservation reservation = reservationRepository.save(
                new Reservation(
                        "예약 신청합니다.",
                        Status.COMPLETE,
                        mentoring,
                        mentee
                )
        );
        int rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
                reservation.getId(),
                rating,
                content
        );
        Member anotherMember = memberRepository.save(new Member(
                "loginId2",
                Gender.MALE,
                "name2",
                new Phone("010-1234-5679"),
                Password.from("password")
        ));
        String accessTokenWithAnotherMember = jwtProvider.createAccessToken(anotherMember.getId(),
                anotherMember.getRole());

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/post-mentorings-id-review-have-not-reserved",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .requestSchema(Schema.schema("ReviewCreateRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessTokenWithAnotherMember)
                .body(requestBody)
                .when()
                .post("/reviews")
                .then().log().all()
                .statusCode(404);
    }

    @DisplayName("이미 리뷰를 작성했던 멘토링에 중복으로 리뷰 작성을 요청하면 400 Bad Request를 반환한다")
    @Test
    void createReviewFail3() {
        // given
        Password password = Password.from("password");
        Member mentor = memberRepository.save(new Member(
                "mentor",
                Gender.MALE,
                "김트레이너",
                new Phone("010-2222-3333"),
                password
        ));
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
                "name",
                new Phone("010-1234-5678"),
                password
        ));
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "content",
                "introduction"
        ));
        Reservation reservation = reservationRepository.save(
                new Reservation(
                        "예약 신청합니다.",
                        Status.COMPLETE,
                        mentoring,
                        mentee
                )
        );
        int rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
                reservation.getId(),
                rating,
                content
        );
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/post-reviews-success-first",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .summary("리뷰 작성 (첫 번째)")
                                .description("첫 번째 리뷰 작성은 성공합니다.")
                                .requestSchema(Schema.schema("ReviewCreateRequest"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(requestBody)
                .when()
                .post("/reviews")
                .then().log().all()
                .statusCode(201);

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/post-mentorings-id-review-already-reviewed",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .requestSchema(Schema.schema("ReviewCreateRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(requestBody)
                .when()
                .post("/reviews")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("멘토링이 완료되지 않은 예약에 리뷰 작성을 요청하면 400 Bad Request를 반환한다")
    @Test
    void createReviewFail4() {
        // given
        Password password = Password.from("password");
        Member mentor = memberRepository.save(new Member(
                "mentor",
                Gender.MALE,
                "김트레이너",
                new Phone("010-2222-3333"),
                password
        ));
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
                "name",
                new Phone("010-1234-5678"),
                password
        ));
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "content",
                "introduction"
        ));
        Reservation reservation = reservationRepository.save(
                new Reservation(
                        "예약 신청합니다.",
                        Status.PENDING,
                        mentoring,
                        mentee
                )
        );
        int rating = 4;
        String content = "전반적으로 좋았습니다.";
        ReviewCreateRequest requestBody = new ReviewCreateRequest(
                reservation.getId(),
                rating,
                content
        );

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/post-reviews-mentoring-not-completed",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .requestSchema(Schema.schema("ReviewCreateRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(requestBody)
                .when()
                .post("/reviews")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("특정 멤버의 리뷰를 모두 조회 성공 시 200 OK를 반환한다")
    @Test
    void findMemberReviews() {
        // given
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
                "name",
                new Phone("010-1234-5678"),
                Password.from("password")
        ));
        Member mentor1 = memberRepository.save(new Member(
                "mentor1Id",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1111-2222"),
                Password.from("password")
        ));
        Member mentor2 = memberRepository.save(new Member(
                "mentor2Id",
                Gender.MALE,
                "박멘토",
                new Phone("010-2222-3333"),
                Password.from("password")
        ));
        Mentoring mentoring1 = mentoringRepository.save(new Mentoring(
                mentor1,
                5000,
                5,
                "한 줄 소개",
                "긴 글 소개"
        ));
        Mentoring mentoring2 = mentoringRepository.save(new Mentoring(
                mentor2,
                5000,
                5,
                "한 줄 소개",
                "긴 글 소개"
        ));
        Reservation reservation1 = reservationRepository.save(new Reservation(
                "예약합니다.",
                Status.COMPLETE,
                mentoring1,
                mentee
        ));
        Reservation reservation2 = reservationRepository.save(new Reservation(
                "예약합니다.",
                Status.COMPLETE,
                mentoring2,
                mentee
        ));
        reviewRepository.save(new Review(
                4,
                "전반적으로 좋았습니다.",
                reservation1,
                mentee
        ));
        reviewRepository.save(new Review(
                4,
                "전반적으로 좋았습니다.",
                reservation2,
                mentee
        ));
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

        // when
        List<MemberReviewGetResponse> response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/get-reviews-mine-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .summary("내 리뷰 조회")
                                .description("내가 작성한 리뷰 목록을 조회합니다. 성공 시 200 OK를 반환합니다.")
                                .responseSchema(Schema.schema("ReviewListResponse"))
                                .responseFields(
                                        fieldWithPath("[].id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("리뷰 ID"),
                                        fieldWithPath("[].createdAt")
                                                .type(JsonFieldType.STRING)
                                                .description("리뷰 작성 날짜 (yyyy-MM-dd)"),
                                        fieldWithPath("[].rating")
                                                .type(JsonFieldType.NUMBER)
                                                .description("리뷰 평점 (1~5)"),
                                        fieldWithPath("[].content")
                                                .type(JsonFieldType.STRING)
                                                .description("리뷰 내용")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .get("/reviews/mine")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(response).hasSize(2);
    }

    @DisplayName("특정 멘토링에 달린 리뷰 조회 성공 시 200 OK를 반환한다")
    @Test
    void findMentoringReviews() {
        // given
        Member mentor = memberRepository.save(new Member(
                "mentorId",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1111-2222"),
                Password.from("password")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "한 줄 소개",
                "긴 글 소개"
        ));
        Member mentee1 = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
                "name",
                new Phone("010-1234-5678"),
                Password.from("password")
        ));
        Member mentee2 = memberRepository.save(new Member(
                "loginId2",
                Gender.MALE,
                "name",
                new Phone("010-1234-5670"),
                Password.from("password")
        ));
        Reservation reservation1 = reservationRepository.save(new Reservation(
                "예약합니다.",
                Status.COMPLETE,
                mentoring,
                mentee1
        ));
        Reservation reservation2 = reservationRepository.save(new Reservation(
                "예약합니다.",
                Status.COMPLETE,
                mentoring,
                mentee2
        ));
        reviewRepository.save(new Review(
                5,
                "전반적으로 좋았습니다.",
                reservation1,
                mentee1
        ));
        reviewRepository.save(new Review(
                2,
                "전반적으로 좋았습니다.",
                reservation2,
                mentee2
        ));
        String accessToken = jwtProvider.createAccessToken(mentee1.getId(), mentee1.getRole());

        // when
        // then
        List<ReviewGetResponse> response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/get-mentorings-id-reviews-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .summary("멘토링 리뷰 조회")
                                .description("특정 멘토링에 작성된 리뷰 목록을 조회합니다. 성공 시 200 OK를 반환합니다.")
                                .responseSchema(Schema.schema("ReviewListResponse"))
                                .responseFields(
                                        fieldWithPath("[].id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("리뷰 ID"),
                                        fieldWithPath("[].reviewerName")
                                                .type(JsonFieldType.STRING)
                                                .description("리뷰 작성자 이름"),
                                        fieldWithPath("[].createdAt")
                                                .type(JsonFieldType.STRING)
                                                .description("리뷰 작성 날짜 (yyyy-MM-dd)"),
                                        fieldWithPath("[].rating")
                                                .type(JsonFieldType.NUMBER)
                                                .description("리뷰 평점 (1~5)"),
                                        fieldWithPath("[].content")
                                                .type(JsonFieldType.STRING)
                                                .description("리뷰 내용")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .get("/mentorings/{mentoringId}/reviews", mentoring.getId())
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(response).hasSize(2);
    }

    @DisplayName("본인이 남긴 리뷰의 별점을 수정 완료하면 200 OK를 반환한다")
    @Test
    void modifyReview1() {
        // given
        Member mentor = memberRepository.save(new Member(
                "mentorId",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1111-2222"),
                Password.from("password")
        ));
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
                "name",
                new Phone("010-1234-5678"),
                Password.from("password")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "한 줄 소개",
                "긴 글 소개"
        ));
        Reservation reservation = reservationRepository.save(new Reservation(
                "예약합니다.",
                Status.COMPLETE,
                mentoring,
                mentee
        ));
        int originalRating = 5;
        String originalContent = "최고의 멘토링이었습니다.";
        Review review = reviewRepository.save(new Review(
                originalRating,
                originalContent,
                reservation,
                mentee
        ));
        int newRating = 2;
        ReviewModifyRequest requestBody = new ReviewModifyRequest(
                newRating,
                null
        );

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/patch-reviews-id-success-rating",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .summary("리뷰 수정 - 별점")
                                .description("리뷰의 별점을 수정합니다. 성공 시 200 OK를 반환합니다.")
                                .requestSchema(Schema.schema("ReviewModifyRequest"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", jwtProvider.createAccessToken(mentee.getId(), mentee.getRole()))
                .body(requestBody)
                .when()
                .patch("/reviews/{reviewId}", review.getId())
                .then().log().all()
                .statusCode(200);

    }

    @DisplayName("본인이 남긴 리뷰의 내용을 수정 완료하면 200 OK를 반환한다")
    @Test
    void modifyReview2() {
        // given
        Member mentor = memberRepository.save(new Member(
                "mentorId",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1111-2222"),
                Password.from("password")
        ));
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
                "name",
                new Phone("010-1234-5678"),
                Password.from("password")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "한 줄 소개",
                "긴 글 소개"
        ));
        Reservation reservation = reservationRepository.save(new Reservation(
                "예약합니다.",
                Status.COMPLETE,
                mentoring,
                mentee
        ));
        int originalRating = 5;
        String originalContent = "최고의 멘토링이었습니다.";
        Review review = reviewRepository.save(new Review(
                originalRating,
                originalContent,
                reservation,
                mentee
        ));
        String newContent = "생각해 보니 비용이 너무 비쌌던 것 같아요";
        ReviewModifyRequest requestBody = new ReviewModifyRequest(
                null,
                newContent
        );

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/patch-reviews-id-success-content",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .summary("리뷰 수정 - 내용")
                                .description("리뷰의 내용을 수정합니다. 성공 시 200 OK를 반환합니다.")
                                .requestSchema(Schema.schema("ReviewModifyRequest"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", jwtProvider.createAccessToken(mentee.getId(), mentee.getRole()))
                .body(requestBody)
                .when()
                .patch("/reviews/{reviewId}", review.getId())
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("본인이 남긴 리뷰의 별점과 내용을 수정 완료하면 200 OK를 반환한다")
    @Test
    void modifyReview() {
        // given
        Member mentor = memberRepository.save(new Member(
                "mentorId",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1111-2222"),
                Password.from("password")
        ));
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
                "name",
                new Phone("010-1234-5678"),
                Password.from("password")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "한 줄 소개",
                "긴 글 소개"
        ));
        Reservation reservation = reservationRepository.save(new Reservation(
                "예약합니다.",
                Status.COMPLETE,
                mentoring,
                mentee
        ));
        int originalRating = 5;
        String originalContent = "최고의 멘토링이었습니다.";
        Review review = reviewRepository.save(new Review(
                originalRating,
                originalContent,
                reservation,
                mentee
        ));
        int newRating = 2;
        String newContent = "생각해 보니 비용이 너무 비쌌던 것 같아요";
        ReviewModifyRequest requestBody = new ReviewModifyRequest(
                newRating,
                newContent
        );

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/patch-reviews-id-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .summary("리뷰 수정")
                                .description("리뷰의 별점과 내용을 수정합니다. 성공 시 200 OK를 반환합니다.")
                                .requestSchema(Schema.schema("ReviewModifyRequest"))
                                .requestFields(
                                        fieldWithPath("rating").type(JsonFieldType.NUMBER).description("평점 (1~5)")
                                                .optional(),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용")
                                                .optional()
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", jwtProvider.createAccessToken(mentee.getId(), mentee.getRole()))
                .body(requestBody)
                .when()
                .patch("/reviews/{reviewId}", review.getId())
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("본인이 작성하지 않은 리뷰를 수정하려고 하면 403 Forbidden를 반환한다")
    @Test
    void modifyReviewFail2() {
        // given
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
                "name",
                new Phone("010-1234-5678"),
                Password.from("password")
        ));
        Member mentor = memberRepository.save(new Member(
                "mentorId",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1111-2222"),
                Password.from("password")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "content",
                "introduction"
        ));
        Reservation reservation = reservationRepository.save(new Reservation(
                "예약합니다.",
                Status.COMPLETE,
                mentoring,
                mentee
        ));
        Review review = reviewRepository.save(new Review(
                4,
                "전반적으로 좋았습니다.",
                reservation,
                mentee
        ));
        Member invalidMember = memberRepository.save(new Member(
                "loginId2",
                Gender.MALE,
                "name2",
                new Phone("010-1234-5679"),
                Password.from("password")
        ));
        ReviewModifyRequest requestBody = new ReviewModifyRequest(
                2,
                "생각해 보니 비용이 너무 비쌌던 것 같아요"
        );

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/patch-reviews-id-not-mine",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .requestSchema(Schema.schema("ReviewModifyRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", jwtProvider.createAccessToken(invalidMember.getId(), invalidMember.getRole()))
                .body(requestBody)
                .when()
                .patch("/reviews/{reviewId}", review.getId())
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("리뷰 삭제에 성공하면 204 NO CONTENT를 반환한다")
    @Test
    void deleteReview() {
        // given
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
                "name",
                new Phone("010-1234-5678"),
                Password.from("password")
        ));
        Member mentor = memberRepository.save(new Member(
                "mentorId",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1111-2222"),
                Password.from("password")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "한 줄 소개",
                "긴 글 소개"
        ));
        Reservation reservation = reservationRepository.save(new Reservation(
                "예약합니다.",
                Status.COMPLETE,
                mentoring,
                mentee
        ));
        Review review = reviewRepository.save(new Review(
                4,
                "전반적으로 좋았습니다.",
                reservation,
                mentee
        ));

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/delete-reviews-id-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .summary("리뷰 삭제")
                                .description("리뷰를 삭제합니다. 성공 시 204 No Content, 실패 시 403 Forbidden 또는 404 Not Found를 반환합니다.")
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", jwtProvider.createAccessToken(mentee.getId(), mentee.getRole()))
                .when()
                .delete("/reviews/{reviewId}", review.getId())
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("존재하지 않는 리뷰 삭제 요청 시 404 NOT FOUND를 반환한다")
    @Test
    void deleteReviewFail1() {
        // given
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
                "name",
                new Phone("010-1234-5678"),
                Password.from("password")
        ));

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/delete-reviews-id-fail-not-found",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .requestSchema(Schema.schema("ErrorResponse"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", jwtProvider.createAccessToken(mentee.getId(), mentee.getRole()))
                .when()
                .delete("/reviews/{reviewId}", 999)
                .then().log().all()
                .statusCode(404);
    }

    @DisplayName("본인이 작성하지 않은 리뷰를 삭제하려고 하면 403 Forbidden를 반환한다")
    @Test
    void deleteReviewFail2() {
        // given
        Member mentee = memberRepository.save(new Member(
                "loginId",
                Gender.MALE,
                "name",
                new Phone("010-1234-5678"),
                Password.from("password")
        ));
        Member mentor = memberRepository.save(new Member(
                "mentorId",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1111-2222"),
                Password.from("password")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "한 줄 소개",
                "긴 글 소개"
        ));
        Reservation reservation = reservationRepository.save(new Reservation(
                "예약합니다.",
                Status.COMPLETE,
                mentoring,
                mentee
        ));
        Review review = reviewRepository.save(new Review(
                4,
                "전반적으로 좋았습니다.",
                reservation,
                mentee
        ));
        Member invalidMember = memberRepository.save(new Member(
                "loginId2",
                Gender.MALE,
                "name2",
                new Phone("010-1234-5679"),
                Password.from("password")
        ));

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("review/delete-reviews-id-not-mine",
                        resource(ResourceSnippetParameters.builder()
                                .tag("리뷰")
                                .requestSchema(Schema.schema("ErrorResponse"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", jwtProvider.createAccessToken(invalidMember.getId(), invalidMember.getRole()))
                .when()
                .delete("/reviews/{reviewId}", review.getId())
                .then().log().all()
                .statusCode(403);
    }
}
