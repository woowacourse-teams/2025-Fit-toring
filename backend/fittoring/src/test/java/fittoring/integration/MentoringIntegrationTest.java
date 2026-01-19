package fittoring.integration;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.image.repository.ImageRepository;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.presentation.dto.request.CertificateInfoRequest;
import fittoring.application.mentoring.presentation.dto.request.MentoringRegisterRequest;
import fittoring.application.mentoring.presentation.dto.response.CertificateSpecAndImageResponse;
import fittoring.application.mentoring.presentation.dto.response.MentoringResponse;
import fittoring.application.mentoring.repository.CategoryMentoringRepository;
import fittoring.application.mentoring.repository.CategoryRepository;
import fittoring.application.mentoring.repository.CertificateRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.mentoring.repository.MentoringStatisticsRepository;
import fittoring.application.mentoring.service.dto.MentoringSummaryPaginationResponse;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.application.review.repository.ReviewRepository;
import fittoring.domain.model.Category;
import fittoring.domain.model.CategoryMentoring;
import fittoring.domain.model.Certificate;
import fittoring.domain.model.CertificateType;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.MentoringStatistics;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import fittoring.domain.model.Status;
import fittoring.domain.model.password.Password;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;

class MentoringIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private MentoringStatisticsRepository mentoringStatisticsRepository;

    @Autowired
    private CategoryMentoringRepository categoryMentoringRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @DisplayName("멘토링 등록에 성공하면 201 Created를 반환한다")
    @Test
    void registerMentoring() throws IOException {
        // given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        categoryRepository.save(new Category("category1"));

        MentoringRegisterRequest requestBody = new MentoringRegisterRequest(
                1000,
                List.of("category1"),
                "멘토링 소개",
                "profileImageUrl",
                3,
                "한 줄 소개",
                List.of(new CertificateInfoRequest(CertificateType.LICENSE, "자격증", "certificateImageUrl"))
        );
        String accessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());

        given(presignedUrlService.isObjectExistsFromKey(anyString()))
                .willReturn(true);

        // when
        // then
        RestAssured
                .given(spec)
                .log().all().contentType(ContentType.JSON)
                .filter(documentWithTag("mentoring/register-mentoring-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("멘토링")
                                .summary("멘토링 등록")
                                .description("새로운 멘토링을 등록합니다. 성공 시 201 Created, 실패 시 400 Bad Request를 반환합니다.")
                                .requestSchema(Schema.schema("MentoringRegisterRequest"))
                                .requestFields(
                                        fieldWithPath("price")
                                                .type(JsonFieldType.NUMBER)
                                                .description("가격"),
                                        fieldWithPath("category")
                                                .type(JsonFieldType.ARRAY)
                                                .description("카테고리 목록"),
                                        fieldWithPath("introduction")
                                                .type(JsonFieldType.STRING)
                                                .description("멘토링 소개"),
                                        fieldWithPath("profileImageUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 이미지 URL"),
                                        fieldWithPath("career")
                                                .type(JsonFieldType.NUMBER)
                                                .description("경력 (년)"),
                                        fieldWithPath("content")
                                                .type(JsonFieldType.STRING)
                                                .description("한 줄 소개"),
                                        fieldWithPath("certificateInfoRequests[].type")
                                                .type(JsonFieldType.STRING)
                                                .description("자격증 타입 (LICENSE, DEGREE, ETC)"),
                                        fieldWithPath("certificateInfoRequests[].title")
                                                .type(JsonFieldType.STRING)
                                                .description("자격증 이름"),
                                        fieldWithPath("certificateInfoRequests[].imageUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("자격증 이미지 URL")
                                )
                                .build())))
                .cookie("accessToken", accessToken)
                .body(objectMapper.writeValueAsString(requestBody))
                .when()
                .post("/mentorings")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("이미 멘토링을 등록한 멘토가 멘토링을 등록하려고 하면 400 Bad Request를 반환한다")
    @Test
    void registerMentoringFail() throws IOException {
        // given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));
        categoryRepository.save(new Category("category1"));

        MentoringRegisterRequest requestBody = new MentoringRegisterRequest(
                1000,
                List.of("category1"),
                "멘토링 소개",
                "profileImageUrl",
                3,
                "한 줄 소개",
                List.of(new CertificateInfoRequest(CertificateType.LICENSE, "자격증", "certificateImageUrl"))
        );
        String accessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());

        given(presignedUrlService.isObjectExistsFromKey(anyString()))
                .willReturn(true);

        // when
        // then
        RestAssured
                .given(spec)
                .log().all().contentType(ContentType.JSON)
                .filter(documentWithTag("mentoring/register-mentoring-fail-duplicate",
                        resource(ResourceSnippetParameters.builder()
                                .tag("멘토링")
                                .requestSchema(Schema.schema("MentoringRegisterRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .cookie("accessToken", accessToken)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(requestBody))
                .when()
                .post("/mentorings")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("개설된 멘토링을 수정 성공하면 200 OK를 반환한다")
    @Test
    void modifyMentoring() throws IOException {
        //given
        Member mentor = memberRepository.save(new Member(
                "id1",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1234-9048"),
                Password.from("pw")
        ));

        Category category1 = categoryRepository.save(new Category("category1"));
        categoryRepository.save(new Category("category2"));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                3,
                "한 줄 소개",
                "긴 글 소개"
        ));
        imageRepository.save(new Image(
                "originalProfileImage",
                ImageType.MENTORING_PROFILE,
                mentoring.getId(),
                null
        ));
        categoryMentoringRepository.save(new CategoryMentoring(category1, mentoring));
        Certificate certificate = certificateRepository.save(new Certificate(
                CertificateType.LICENSE,
                "운전면허증",
                mentoring
        ));
        imageRepository.save(new Image(
                "originalCertificateImage",
                ImageType.CERTIFICATE,
                certificate.getId(),
                null
        ));

        int newPrice = 1000;
        String newCategory = "category2";
        String newIntroduction = "수정된 긴 글 소개";
        String newProfileImageUrl = "수정된 새로운 프로필 이미지";
        int newCareer = 5;
        String newContent = "수정된 한 줄 소개";
        MentoringRegisterRequest requestBody = new MentoringRegisterRequest(
                newPrice,
                List.of(newCategory),
                newIntroduction,
                newProfileImageUrl,
                newCareer,
                newContent,
                Collections.emptyList()
        );
        String accessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());

        given(presignedUrlService.isObjectExistsFromKey(anyString()))
                .willReturn(true);

        // when
        // then
        RestAssured
                .given(spec)
                .log().all().contentType(ContentType.JSON)
                .filter(documentWithTag("mentoring/modift-mentoring-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("멘토링")
                                .summary("멘토링 수정")
                                .description(
                                        "등록된 멘토링 정보를 수정합니다. 성공 시 200 OK, 실패 시 403 Forbidden 또는 404 Not Found를 반환합니다.")
                                .requestSchema(Schema.schema("MentoringModifyRequest"))
                                .requestFields(
                                        fieldWithPath("price")
                                                .type(JsonFieldType.NUMBER)
                                                .description("가격"),
                                        fieldWithPath("category")
                                                .type(JsonFieldType.ARRAY)
                                                .description("카테고리 목록"),
                                        fieldWithPath("introduction")
                                                .type(JsonFieldType.STRING)
                                                .description("멘토링 소개"),
                                        fieldWithPath("career")
                                                .type(JsonFieldType.NUMBER)
                                                .description("경력 (년)"),
                                        fieldWithPath("content")
                                                .type(JsonFieldType.STRING)
                                                .description("한 줄 소개"),
                                        fieldWithPath("profileImageUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 이미지 URL"),
                                        fieldWithPath("certificateInfoRequests")
                                                .type(JsonFieldType.ARRAY)
                                                .description("수정할 자격증 목록")
                                                .optional(),
                                        fieldWithPath("certificateInfoRequests[].type")
                                                .type(JsonFieldType.STRING)
                                                .description("자격증 타입 (LICENSE, DEGREE, ETC)")
                                                .optional(),
                                        fieldWithPath("certificateInfoRequests[].title")
                                                .type(JsonFieldType.STRING)
                                                .description("자격증 이름")
                                                .optional(),
                                        fieldWithPath("certificateInfoRequests[].imageUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("자격증 이미지 URL")
                                                .optional()
                                )
                                .pathParameters(
                                        parameterWithName("mentoringId")
                                                .description("수정할 멘토링 ID")
                                )
                                .build())))
                .cookie("accessToken", accessToken)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(requestBody))
                .when()
                .put("/mentorings/{mentoringId}", mentoring.getId())
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("존재하지 않는 멘토링을 수정하려고 하면 404 Not Found를 반환한다")
    @Test
    void modifyMentoringFail1() throws IOException {
        // given
        Member mentor = memberRepository.save(new Member(
                "id1",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1234-9048"),
                Password.from("pw")
        ));

        int newPrice = 1000;
        String newCategory = "category2";
        String newIntroduction = "수정된 긴 글 소개";
        String newProfileImageUrl = "수정된 새로운 프로필 이미지";
        int newCareer = 5;
        String newContent = "수정된 한 줄 소개";
        MentoringRegisterRequest requestBody = new MentoringRegisterRequest(
                newPrice,
                List.of(newCategory),
                newIntroduction,
                newProfileImageUrl,
                newCareer,
                newContent,
                Collections.emptyList()
        );
        String accessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());

        // when
        // then
        RestAssured
                .given(spec)
                .log().all().contentType(ContentType.JSON)
                .filter(documentWithTag("mentoring/modift-mentoring-fail-not-found",
                        resource(ResourceSnippetParameters.builder()
                                .tag("멘토링")
                                .requestSchema(Schema.schema("MentoringModifyRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .cookie("accessToken", accessToken)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(requestBody))
                .when()
                .put("/mentorings/{mentoringId}", 999)
                .then().log().all()
                .statusCode(404);
    }

    @DisplayName("본인이 개설하지 않은 멘토링을 수정하려고 하면 403 Forbidden를 반환한다")
    @Test
    void modifyMentoringFail2() throws IOException {
        // given
        Member mentor = memberRepository.save(new Member(
                "id1",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1234-9048"),
                Password.from("pw")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                3,
                "한 줄 소개",
                "긴 글 소개"
        ));

        Member invalidMember = memberRepository.save(new Member(
                "id2",
                Gender.MALE,
                "박트레이너",
                new Phone("010-1234-9021"),
                Password.from("pw")
        ));

        int newPrice = 1000;
        String newCategory = "category2";
        String newIntroduction = "수정된 긴 글 소개";
        String newProfileImageUrl = "수정된 새로운 프로필 이미지";
        int newCareer = 5;
        String newContent = "수정된 한 줄 소개";
        MentoringRegisterRequest requestBody = new MentoringRegisterRequest(
                newPrice,
                List.of(newCategory),
                newIntroduction,
                newProfileImageUrl,
                newCareer,
                newContent,
                Collections.emptyList()
        );
        String accessToken = jwtProvider.createAccessToken(invalidMember.getId(), invalidMember.getRole());

        // when
        // then
        RestAssured
                .given(spec)
                .log().all().contentType(ContentType.JSON)
                .filter(documentWithTag("mentoring/modift-mentoring-fail-forbidden",
                        resource(ResourceSnippetParameters.builder()
                                .tag("멘토링")
                                .requestSchema(Schema.schema("MentoringModifyRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .cookie("accessToken", accessToken)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(requestBody))
                .when()
                .put("/mentorings/{mentoringId}", mentoring.getId())
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("멘토링 목록 조회 API 테스트")
    @Nested
    class FindMentoring {

        @DisplayName("멘토링 Id로 멘토링 조회에 성공하면 200 OK 상태코드와 멘토링 정보를 반환한다.")
        @Test
        void getMentoring() {
            //given
            Member mentee = memberRepository.save(
                    new Member("id", Gender.MALE, "멘티1", new Phone("010-1231-1231"), Password.from("pw")));
            String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

            Member mentor1 = memberRepository.save(
                    new Member("id1", Gender.MALE, "멘토1", new Phone("010-1234-5678"), Password.from("pw")));
            Member mentor2 = memberRepository.save(
                    new Member("id2", Gender.MALE, "멘토2", new Phone("010-1111-2222"), Password.from("pw")));

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            mentor1,
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );
            mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(savedMentoring));

            Mentoring savedMentoring2 = mentoringRepository.save(
                    new Mentoring(
                            mentor2,
                            1000,
                            4,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );
            mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(savedMentoring2));

            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring2));

            imageRepository.save(new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentoring.getId(),
                    null));
            imageRepository.save(new Image("image2.jpg", ImageType.MENTORING_PROFILE, savedMentoring2.getId(),
                    null));

            Reservation savedReservation1 = reservationRepository.save(
                    new Reservation("예약 코멘트1", Status.COMPLETE, savedMentoring, mentee));
            Reservation savedReservation2 = reservationRepository.save(
                    new Reservation("예약 코멘트2", Status.COMPLETE, savedMentoring, mentee));
            mentoringStatisticsRepository.updateReservationCountPlus(savedMentoring.getId());
            mentoringStatisticsRepository.updateReservationCountPlus(savedMentoring2.getId());

            reviewRepository.save(new Review(4, "리뷰 코멘트", savedReservation1, mentee));
            reviewRepository.save(new Review(5, "리뷰 코멘트", savedReservation2, mentee));
            mentoringStatisticsRepository.updateReviewStatisticsPlus(savedMentoring.getId(), 4);
            mentoringStatisticsRepository.updateReviewStatisticsPlus(savedMentoring.getId(), 5);

            Long mentoringId = savedMentoring.getId();

            //when
            MentoringResponse response = RestAssured
                    .given(spec)
                    .filter(documentWithTag("mentoring/get-mentoring-success",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .summary("멘토링 상세 조회")
                                    .description("멘토링의 상세 정보를 조회합니다. 성공 시 200 OK, 실패 시 404 Not Found를 반환합니다.")
                                    .responseSchema(Schema.schema("MentoringResponse"))
                                    .responseFields(
                                            fieldWithPath("id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("멘토링 ID"),
                                            fieldWithPath("mentorName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("멘토 이름"),
                                            fieldWithPath("categories[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("멘토링 카테고리 목록"),
                                            fieldWithPath("price")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("멘토링 가격"),
                                            fieldWithPath("career")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("멘토 경력 (년차)"),
                                            fieldWithPath("profileImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("멘토 프로필 이미지 URL")
                                                    .optional(),
                                            fieldWithPath("introduction")
                                                    .type(JsonFieldType.STRING)
                                                    .description("멘토 한 줄 소개"),
                                            fieldWithPath("content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("멘토링 상세 설명"),
                                            fieldWithPath("certificates")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("멘토가 보유한 자격증 목록"),
                                            fieldWithPath("certificates[].certificateId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("자격증 ID")
                                                    .optional(),
                                            fieldWithPath("certificates[].title")
                                                    .type(JsonFieldType.STRING)
                                                    .description("자격증 이름")
                                                    .optional(),
                                            fieldWithPath("certificates[].type")
                                                    .type(JsonFieldType.STRING)
                                                    .description("자격증 유형")
                                                    .optional(),
                                            fieldWithPath("certificates[].imageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("자격증 이미지 URL")
                                                    .optional(),
                                            fieldWithPath("ratingAverage")
                                                    .type(JsonFieldType.STRING)
                                                    .description("평균 평점 (소수점 1자리)"),

                                            fieldWithPath("ratingCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("리뷰 개수")
                                    )
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", accessToken)
                    .queryParam("categoryTitle1", savedCategory.getTitle())
                    .queryParam("categoryTitle2", savedCategory2.getTitle())
                    .when()
                    .get("/mentorings/{mentoringId}", mentoringId)
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringResponse.class);

            //then
            MentoringResponse expected = new MentoringResponse(
                    savedMentoring.getId(),
                    savedMentoring.getMentorName(),
                    List.of(savedCategory.getTitle()),
                    savedMentoring.getPrice(),
                    savedMentoring.getCareer(),
                    "image1.jpg",
                    savedMentoring.getIntroduction(),
                    savedMentoring.getContent(),
                    new ArrayList<>(),
                    String.format("%.1f", 4.5),
                    2
            );
            assertThat(response).isNotNull().isEqualTo(expected);
        }

        @DisplayName("현재 로그인한 멘토는 자신이 개설한 멘토링을 조회할 수 있다.")
        @Test
        void getMentoringByMentorId() {
            //given

            //멘토 생성
            Member mentor = new Member("id1", Gender.MALE, "멘토1", new Phone("010-1234-5678"), Password.from("pw"));
            Member savedMentor = memberRepository.save(mentor);

            //토큰 생성
            String accessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            savedMentor,
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );
            mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(savedMentoring));

            //카테고리 생성
            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

            //멘토링 카테고리 생성
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring));

            //멘토링 프로필 이미지 생성
            Image savedImage = imageRepository.save(
                    new Image(
                            "image1.jpg",
                            ImageType.MENTORING_PROFILE,
                            savedMentoring.getId(),
                            null
                    ));

            //자격증 생성
            Certificate certificate = new Certificate(
                    CertificateType.LICENSE,
                    "자격증",
                    savedMentoring
            );
            Certificate certificate2 = new Certificate(
                    CertificateType.LICENSE,
                    "자격증",
                    savedMentoring
            );
            certificate.approve();
            certificate2.approve();

            Certificate savedCertificate = certificateRepository.save(certificate);
            Certificate savedCertificate2 = certificateRepository.save(certificate2);

            //자격증 이미지 생성
            Image savedCetificateImage = imageRepository.save(new Image(
                    "profileImageUrl",
                    ImageType.CERTIFICATE,
                    savedCertificate.getId(),
                    null
            ));

            Image savedCertificateImage2 = imageRepository.save(new Image(
                    "profileImageUrl2",
                    ImageType.CERTIFICATE,
                    savedCertificate2.getId(),
                    null
            ));

            //when
            MentoringResponse response = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-mine-success",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .summary("내 멘토링 조회")
                                    .description("로그인한 멘토가 개설한 멘토링을 조회합니다. 성공 시 200 OK를 반환합니다.")
                                    .responseSchema(Schema.schema("MentoringResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", accessToken)
                    .when()
                    .get("/mentorings/mine")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringResponse.class);

            //then
            List<CertificateSpecAndImageResponse> certificateSpecAndImageResponses = new ArrayList<>();

            certificateSpecAndImageResponses.add(
                    CertificateSpecAndImageResponse.of(certificate, savedCetificateImage.getUrl())
            );
            certificateSpecAndImageResponses.add(
                    CertificateSpecAndImageResponse.of(certificate2, savedCertificateImage2.getUrl())
            );

            assertThat(response.id()).isEqualTo(savedMentoring.getId());
            assertThat(response.mentorName()).isEqualTo(savedMentor.getName());
            assertThat(response.categories()).isEqualTo(List.of("체형교정", "근육증진"));
            assertThat(response.price()).isEqualTo(savedMentoring.getPrice());
            assertThat(response.career()).isEqualTo(savedMentoring.getCareer());
            assertThat(response.introduction()).isEqualTo(savedMentoring.getIntroduction());
            assertThat(response.content()).isEqualTo(savedMentoring.getContent());
            assertThat(response.certificates()).isEqualTo(certificateSpecAndImageResponses);
            assertThat(response.profileImageUrl()).isEqualTo(savedImage.getUrl());
        }

        @DisplayName("존재하지 않는 멘토링 Id로 멘토링 조회에 실패하면 404 Not Found 상태코드를 반환한다.")
        @Test
        void getMentoring2() {
            //given
            Member mentee = memberRepository.save(
                    new Member("id", Gender.MALE, "멘티1", new Phone("010-1231-1231"), Password.from("pw")));
            String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

            Member mentor1 = memberRepository.save(
                    new Member("id1", Gender.MALE, "멘토1", new Phone("010-1234-5678"), Password.from("pw")));
            Member mentor2 = memberRepository.save(
                    new Member("id2", Gender.MALE, "멘토2", new Phone("010-1111-2222"), Password.from("pw")));

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            mentor1,
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );

            Mentoring savedMentoring2 = mentoringRepository.save(
                    new Mentoring(
                            mentor2,
                            1000,
                            4,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );

            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring2));

            imageRepository.save(new Image(
                    "image1.jpg",
                    ImageType.MENTORING_PROFILE,
                    savedMentoring.getId(),
                    null
            ));
            imageRepository.save(new Image(
                    "image2.jpg",
                    ImageType.MENTORING_PROFILE,
                    savedMentoring2.getId(),
                    null
            ));

            long invalidId = 100L;

            //when
            Response response = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-id-not-found",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("ErrorResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", accessToken)
                    .queryParam("categoryTitle1", savedCategory.getTitle())
                    .queryParam("categoryTitle2", savedCategory.getTitle())
                    .when()
                    .get("/mentorings/{mentoringId}", invalidId);

            //then
            String responseMessage = response.jsonPath().getString("message");
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(404);
                softly.assertThat(responseMessage).isEqualTo(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage());
            });
        }
    }

    @Nested
    @DisplayName("멘토링 페이징 조회")
    class getMentoringWithPagination {
        @DisplayName("첫 멘토링 목록 페이지를 조회하고, 반환된 커서를 사용하면 나머지 목록을 조회할 수 있다.")
        @Test
        void getMentoringSummaryPages() {
            //given
            Member mentee = memberRepository.save(
                    new Member("menteeId", Gender.MALE, "멘티1", new Phone("010-1231-1231"), Password.from("pw")));

            List<String> phoneNumbers = List.of(
                    "010-1234-5678",
                    "010-2345-6789",
                    "010-3456-7890",
                    "010-4567-8901",
                    "010-5678-9012",
                    "010-6789-0123",
                    "010-7890-1234",
                    "010-8901-2345",
                    "010-9012-3456",
                    "010-1122-3344",
                    "010-2233-4455",
                    "010-3344-5566"
            );
            List<Member> savedMentor = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Member mentor = new Member(
                        "mentorId" + i,
                        Gender.MALE,
                        "멘토" + i,
                        new Phone(phoneNumbers.get(i)),
                        Password.from("pw"));
                savedMentor.add(mentor);
            }
            memberRepository.saveAll(savedMentor);

            List<Mentoring> savedMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Mentoring mentoring =
                        new Mentoring(
                                savedMentor.get(i),
                                1000,
                                3,
                                "멘토링 내용: " + i,
                                "멘토링 자기소개"
                        );
                savedMentorings.add(mentoring);
            }
            mentoringRepository.saveAll(savedMentorings);

            List<MentoringStatistics> savedMentoringStatistics = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                MentoringStatistics mentoringStatistics = MentoringStatistics.defaultOf(savedMentorings.get(i));
                savedMentoringStatistics.add(mentoringStatistics);
            }
            mentoringStatisticsRepository.saveAll(savedMentoringStatistics);

            Category savedCategory = categoryRepository.save(new Category("체형교정"));

            List<CategoryMentoring> categoryMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                CategoryMentoring categoryMentoring = new CategoryMentoring(savedCategory, savedMentorings.get(i));
                categoryMentorings.add(categoryMentoring);
            }
            categoryMentoringRepository.saveAll(categoryMentorings);

            List<Image> images = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Image image = new Image(
                        "image1.jpg",
                        ImageType.MENTORING_PROFILE,
                        savedMentorings.get(i).getId(),
                        null
                );
                images.add(image);
            }
            imageRepository.saveAll(images);

            List<Reservation> reservations = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Reservation reservation = new Reservation("content", Status.COMPLETE, savedMentorings.get(i), mentee);
                reservations.add(reservation);
            }
            reservationRepository.saveAll(reservations);

            List<Review> reviews = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Review review = new Review(
                        5,
                        "최고의 멘토링이었습니다.",
                        reservations.get(i),
                        mentee
                );
                reviews.add(review);
            }
            reviewRepository.saveAll(reviews);

            //when
            MentoringSummaryPaginationResponse firstResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-success",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .summary("멘토링 목록 페이징 조회")
                                    .description("""
                                            멘토링 목록을 커서 기반 페이징으로 조회합니다.
                                            
                                            - 정렬 기준: CREATED_AT (최신순), RESERVATION_COUNT (예약순), AVERAGE_RATING (평점순)
                                            - 카테고리 필터링: categoryIds 파라미터로 여러 카테고리 ID를 전달하면 해당 카테고리를 모두 포함하는 멘토링만 조회
                                            - 페이징: cursorCode를 사용하여 다음 페이지 조회
                                            
                                            성공 시 200 OK, 잘못된 커서 또는 정렬 키 시 400 Bad Request를 반환합니다.""")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .responseFields(
                                            fieldWithPath("mentoringSummaryResponses[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("멘토링 요약 정보 목록"),

                                            fieldWithPath("mentoringSummaryResponses[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("멘토링 ID"),

                                            fieldWithPath("mentoringSummaryResponses[].mentorName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("멘토 이름"),

                                            fieldWithPath("mentoringSummaryResponses[].categories[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("멘토링 카테고리 목록"),

                                            fieldWithPath("mentoringSummaryResponses[].price")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("멘토링 가격"),

                                            fieldWithPath("mentoringSummaryResponses[].career")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("멘토 경력 (년차)"),

                                            fieldWithPath("mentoringSummaryResponses[].profileImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("멘토 프로필 이미지 URL")
                                                    .optional(),

                                            fieldWithPath("mentoringSummaryResponses[].introduction")
                                                    .type(JsonFieldType.STRING)
                                                    .description("멘토링 한 줄 소개"),

                                            fieldWithPath("mentoringSummaryResponses[].ratingAverage")
                                                    .type(JsonFieldType.STRING)
                                                    .description("평균 평점 (소수점 1자리)")
                                                    .optional(),

                                            fieldWithPath("mentoringSummaryResponses[].ratingCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("리뷰 개수"),

                                            fieldWithPath("nextCursorCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("다음 페이지 조회를 위한 커서 코드 (마지막 페이지인 경우 null)")
                                                    .optional(),

                                            fieldWithPath("hasNext")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("다음 페이지 존재 여부")
                                    )
                                    .queryParameters(
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 기준 (CREATED_AT: 최신순, RESERVATION_COUNT: 예약순, AVERAGE_RATING: 평점순)")
                                                    .defaultValue("CREATED_AT"),
                                            parameterWithName("cursorCode")
                                                    .description("다음 페이지 조회를 위한 커서 코드")
                                                    .optional(),
                                            parameterWithName("categoryIds")
                                                    .description("필터링할 카테고리 ID 목록 (쉼표로 구분)")
                                                    .optional()
                                    )
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "CREATED_AT")
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);

            String nextCursorCode = firstResponse.nextCursorCode();
            MentoringSummaryPaginationResponse nextResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-with-cursor",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "CREATED_AT")
                    .queryParam("cursorCode", nextCursorCode)
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);
            //then
            SoftAssertions.assertSoftly(softAssertions -> {
                assertThat(firstResponse.mentoringSummaryResponses()).hasSize(10);
                assertThat(firstResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(3L);
                assertThat(firstResponse.nextCursorCode()).isNotNull();
                assertThat(firstResponse.hasNext()).isTrue();
                assertThat(nextResponse.mentoringSummaryResponses()).hasSize(2);
                assertThat(nextResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(1L);
                assertThat(nextResponse.nextCursorCode()).isNull();
                assertThat(nextResponse.hasNext()).isFalse();
            });
        }

        @DisplayName("첫 멘토링 목록 페이지를 조회하고, 카테고리 필터링을 할 수 있다.")
        @Test
        void getMentoringSummaryPagesWithCategory_1() {
            //given
            Member mentee = memberRepository.save(
                    new Member("menteeId", Gender.MALE, "멘티1", new Phone("010-1231-1231"), Password.from("pw")));

            List<String> phoneNumbers = List.of(
                    "111-1234-5678",
                    "111-2345-6789",
                    "111-3456-7890",
                    "111-4567-8901",
                    "111-5678-9012",
                    "111-6789-0123",
                    "111-7890-1234",
                    "111-8901-2345",
                    "111-9012-3456",
                    "111-1122-3344",
                    "111-2233-4455",
                    "111-3344-5566"
            );
            List<Member> savedMentor = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Member mentor = new Member(
                        "mentorId" + i,
                        Gender.MALE,
                        "멘토" + i,
                        new Phone(phoneNumbers.get(i)),
                        Password.from("pw"));
                savedMentor.add(mentor);
            }
            memberRepository.saveAll(savedMentor);

            List<Mentoring> savedMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Mentoring mentoring = new Mentoring(
                        savedMentor.get(i),
                        1000,
                        3,
                        "멘토링 내용: " + i,
                        "멘토링 자기소개"
                );
                savedMentorings.add(mentoring);
            }
            mentoringRepository.saveAll(savedMentorings);

            List<MentoringStatistics> savedMentoringStatistics = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                MentoringStatistics mentoringStatistics = MentoringStatistics.defaultOf(savedMentorings.get(i));
                savedMentoringStatistics.add(mentoringStatistics);
            }
            mentoringStatisticsRepository.saveAll(savedMentoringStatistics);

            Category category1 = categoryRepository.save(new Category("체형교정"));
            Category category2 = categoryRepository.save(new Category("다이어트"));

            List<CategoryMentoring> categoryMentorings = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                CategoryMentoring categoryMentoring = new CategoryMentoring(category1, savedMentorings.get(i));
                categoryMentorings.add(categoryMentoring);
            }
            for (int i = 0; i < 5; i++) {
                CategoryMentoring categoryMentoring = new CategoryMentoring(category2, savedMentorings.get(i));
                categoryMentorings.add(categoryMentoring);
            }
            categoryMentoringRepository.saveAll(categoryMentorings);

            List<Image> images = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Image image = new Image(
                        "image1.jpg",
                        ImageType.MENTORING_PROFILE,
                        savedMentorings.get(i).getId(),
                        null
                );
                images.add(image);
            }
            imageRepository.saveAll(images);

            List<Reservation> reservations = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Reservation reservation = new Reservation("content", Status.COMPLETE, savedMentorings.get(i), mentee);
                reservations.add(reservation);
            }
            reservationRepository.saveAll(reservations);

            List<Review> reviews = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Review review = new Review(
                        5,
                        "최고의 멘토링이었습니다.",
                        reservations.get(i),
                        mentee
                );
                reviews.add(review);
            }
            reviewRepository.saveAll(reviews);

            //when
            MentoringSummaryPaginationResponse firstResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-single-category",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "CREATED_AT")
                    .queryParam("categoryIds", "1, 2")
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);

            //then
            SoftAssertions.assertSoftly(softAssertions -> {
                assertThat(firstResponse.mentoringSummaryResponses()).hasSize(5);   // 카테고리 1, 2번 동시 보유 조건
                assertThat(firstResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(1L);
                assertThat(firstResponse.nextCursorCode()).isNull();
                assertThat(firstResponse.hasNext()).isFalse();
            });
        }

        @DisplayName("첫 멘토링 목록 페이지를 조회하고, 카테고리 필터링을 할 수 있다._2")
        @Test
        void getMentoringSummaryPagesWithCategory() {
            //given
            Member mentee = memberRepository.save(
                    new Member("menteeId", Gender.MALE, "멘티1", new Phone("010-1231-1231"), Password.from("pw")));

            List<String> phoneNumbers = List.of(
                    "111-1234-5678",
                    "111-2345-6789",
                    "111-3456-7890",
                    "111-4567-8901",
                    "111-5678-9012",
                    "111-6789-0123",
                    "111-7890-1234",
                    "111-8901-2345",
                    "111-9012-3456",
                    "111-1122-3344",
                    "111-2233-4455",
                    "111-3344-5566"
            );
            List<Member> savedMentor = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Member mentor = new Member(
                        "mentorId" + i,
                        Gender.MALE,
                        "멘토" + i,
                        new Phone(phoneNumbers.get(i)),
                        Password.from("pw"));
                savedMentor.add(mentor);
            }
            memberRepository.saveAll(savedMentor);

            List<Mentoring> savedMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Mentoring mentoring =
                        new Mentoring(
                                savedMentor.get(i),
                                1000,
                                3,
                                "멘토링 내용: " + i,
                                "멘토링 자기소개"
                        );
                savedMentorings.add(mentoring);
            }
            mentoringRepository.saveAll(savedMentorings);

            List<MentoringStatistics> savedMentoringStatistics = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                MentoringStatistics mentoringStatistics = MentoringStatistics.defaultOf(savedMentorings.get(i));
                savedMentoringStatistics.add(mentoringStatistics);
            }
            mentoringStatisticsRepository.saveAll(savedMentoringStatistics);

            Category category1 = categoryRepository.save(new Category("체형교정"));
            Category category2 = categoryRepository.save(new Category("다이어트"));

            List<CategoryMentoring> categoryMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                CategoryMentoring categoryMentoring1 = new CategoryMentoring(category1, savedMentorings.get(i));
                categoryMentorings.add(categoryMentoring1);
                CategoryMentoring categoryMentoring2 = new CategoryMentoring(category2, savedMentorings.get(i));
                categoryMentorings.add(categoryMentoring2);
            }
            categoryMentoringRepository.saveAll(categoryMentorings);

            List<Image> images = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Image image = new Image(
                        "image1.jpg",
                        ImageType.MENTORING_PROFILE,
                        savedMentorings.get(i).getId(),
                        null
                );
                images.add(image);
            }
            imageRepository.saveAll(images);

            List<Reservation> reservations = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Reservation reservation = new Reservation("content", Status.COMPLETE, savedMentorings.get(i), mentee);
                reservations.add(reservation);
            }
            reservationRepository.saveAll(reservations);

            List<Review> reviews = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Review review = new Review(
                        5,
                        "최고의 멘토링이었습니다.",
                        reservations.get(i),
                        mentee
                );
                reviews.add(review);
            }
            reviewRepository.saveAll(reviews);

            //when
            MentoringSummaryPaginationResponse firstResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-multi-category-first",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "CREATED_AT")
                    .queryParam("categoryIds", "1, 2")
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);

            String nextCursorCode = firstResponse.nextCursorCode();
            MentoringSummaryPaginationResponse nextResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-multi-category-next",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "CREATED_AT")
                    .queryParam("categoryIds", "1, 2")
                    .queryParam("cursorCode", nextCursorCode)
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);
            //then
            SoftAssertions.assertSoftly(softAssertions -> {
                assertThat(firstResponse.mentoringSummaryResponses()).hasSize(10);   // 카테고리 1, 2번 동시 보유 조건
                assertThat(firstResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(3L);
                assertThat(firstResponse.nextCursorCode()).isNotNull();
                assertThat(firstResponse.hasNext()).isTrue();
                assertThat(nextResponse.mentoringSummaryResponses()).hasSize(2);
                assertThat(nextResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(1L);
                assertThat(nextResponse.nextCursorCode()).isNull();
                assertThat(nextResponse.hasNext()).isFalse();
            });
        }

        @DisplayName("첫 멘토링 목록 페이지를 예약 개수 많은 순으로 조회하고, 반환된 커서를 사용하면 나머지 목록을 조회할 수 있다.")
        @Test
        void getMentoringSummaryPages2() {
            //given
            Member mentee = memberRepository.save(
                    new Member("menteeId", Gender.MALE, "멘티1", new Phone("010-1231-1231"), Password.from("pw")));

            List<String> phoneNumbers = List.of(
                    "010-1234-5678",
                    "010-2345-6789",
                    "010-3456-7890",
                    "010-4567-8901",
                    "010-5678-9012",
                    "010-6789-0123",
                    "010-7890-1234",
                    "010-8901-2345",
                    "010-9012-3456",
                    "010-1122-3344",
                    "010-2233-4455",
                    "010-3344-5566"
            );
            List<Member> savedMentor = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Member mentor = new Member(
                        "mentorId" + i,
                        Gender.MALE,
                        "멘토" + i,
                        new Phone(phoneNumbers.get(i)),
                        Password.from("pw"));
                savedMentor.add(mentor);
            }
            memberRepository.saveAll(savedMentor);

            List<Mentoring> savedMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Mentoring mentoring =
                        new Mentoring(
                                savedMentor.get(i),
                                1000,
                                3,
                                "멘토링 내용: " + i,
                                "멘토링 자기소개"
                        );
                savedMentorings.add(mentoring);
            }
            mentoringRepository.saveAll(savedMentorings);

            List<MentoringStatistics> savedMentoringStatistics = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                MentoringStatistics mentoringStatistics = MentoringStatistics.defaultOf(savedMentorings.get(i));
                savedMentoringStatistics.add(mentoringStatistics);
            }
            mentoringStatisticsRepository.saveAll(savedMentoringStatistics);

            Category savedCategory = categoryRepository.save(new Category("체형교정"));

            List<CategoryMentoring> categoryMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                CategoryMentoring categoryMentoring = new CategoryMentoring(savedCategory, savedMentorings.get(i));
                categoryMentorings.add(categoryMentoring);
            }
            categoryMentoringRepository.saveAll(categoryMentorings);

            List<Image> images = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Image image = new Image(
                        "image1.jpg",
                        ImageType.MENTORING_PROFILE,
                        savedMentorings.get(i).getId(),
                        null
                );
                images.add(image);
            }
            imageRepository.saveAll(images);

            List<Reservation> reservations = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                for (int k = 12 - i; k >= 0; k--) {
                    Reservation reservation = new Reservation("content", Status.COMPLETE, savedMentorings.get(i),
                            mentee);
                    reservations.add(reservation);
                    mentoringStatisticsRepository.updateReservationCountPlus(savedMentorings.get(i).getId());
                }
            }
            reservationRepository.saveAll(reservations);

            List<Review> reviews = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Review review = new Review(
                        5,
                        "최고의 멘토링이었습니다.",
                        reservations.get(i),
                        mentee
                );
                reviews.add(review);
                mentoringStatisticsRepository.updateReviewStatisticsPlus(reservations.get(i).getMentoring().getId(), 5);
            }
            reviewRepository.saveAll(reviews);

            //when
            MentoringSummaryPaginationResponse firstResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-reservation-count-first",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "RESERVATION_COUNT")
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);

            String nextCursorCode = firstResponse.nextCursorCode();
            MentoringSummaryPaginationResponse nextResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-reservation-count-next",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "RESERVATION_COUNT")
                    .queryParam("cursorCode", nextCursorCode)
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);

            //then
            SoftAssertions.assertSoftly(softAssertions -> {
                assertThat(firstResponse.mentoringSummaryResponses()).hasSize(10);
                assertThat(firstResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(10L);
                assertThat(firstResponse.nextCursorCode()).isNotNull();
                assertThat(firstResponse.hasNext()).isTrue();
                assertThat(nextResponse.mentoringSummaryResponses()).hasSize(2);
                assertThat(nextResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(12L);
                assertThat(nextResponse.nextCursorCode()).isNull();
                assertThat(nextResponse.hasNext()).isFalse();
            });
        }

        @DisplayName("첫 멘토링 목록 페이지를 예약 개수 많은 순으로 조회하고, 카테고리 필터링을 할 수 있다.")
        @Test
        void getMentoringSummaryPagesWithCategory2() {
            //given
            Member mentee = memberRepository.save(
                    new Member("menteeId", Gender.MALE, "멘티1", new Phone("010-1231-1231"), Password.from("pw")));

            List<String> phoneNumbers = List.of(
                    "111-1234-5678",
                    "111-2345-6789",
                    "111-3456-7890",
                    "111-4567-8901",
                    "111-5678-9012",
                    "111-6789-0123",
                    "111-7890-1234",
                    "111-8901-2345",
                    "111-9012-3456",
                    "111-1122-3344",
                    "111-2233-4455",
                    "111-3344-5566"
            );
            List<Member> savedMentor = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Member mentor = new Member(
                        "mentorId" + i,
                        Gender.MALE,
                        "멘토" + i,
                        new Phone(phoneNumbers.get(i)),
                        Password.from("pw"));
                savedMentor.add(mentor);
            }
            memberRepository.saveAll(savedMentor);

            List<Mentoring> savedMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Mentoring mentoring =
                        new Mentoring(
                                savedMentor.get(i),
                                1000,
                                3,
                                "멘토링 내용: " + i,
                                "멘토링 자기소개"
                        );
                savedMentorings.add(mentoring);
            }
            mentoringRepository.saveAll(savedMentorings);

            List<MentoringStatistics> savedMentoringStatistics = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                MentoringStatistics mentoringStatistics = MentoringStatistics.defaultOf(savedMentorings.get(i));
                savedMentoringStatistics.add(mentoringStatistics);
            }
            mentoringStatisticsRepository.saveAll(savedMentoringStatistics);

            Category category1 = categoryRepository.save(new Category("체형교정"));
            Category category2 = categoryRepository.save(new Category("다이어트"));

            List<CategoryMentoring> categoryMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                CategoryMentoring categoryMentoring1 = new CategoryMentoring(category1, savedMentorings.get(i));
                categoryMentorings.add(categoryMentoring1);
                CategoryMentoring categoryMentoring2 = new CategoryMentoring(category2, savedMentorings.get(i));
                categoryMentorings.add(categoryMentoring2);
            }
            categoryMentoringRepository.saveAll(categoryMentorings);

            List<Image> images = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Image image = new Image(
                        "image1.jpg",
                        ImageType.MENTORING_PROFILE,
                        savedMentorings.get(i).getId(),
                        null
                );
                images.add(image);
            }
            imageRepository.saveAll(images);

            List<Reservation> reservations = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                for (int k = 12 - i; k >= 0; k--) {
                    Reservation reservation = new Reservation("content", Status.COMPLETE, savedMentorings.get(i),
                            mentee);
                    reservations.add(reservation);
                    mentoringStatisticsRepository.updateReservationCountPlus(savedMentorings.get(i).getId());
                }
            }
            reservationRepository.saveAll(reservations);

            List<Review> reviews = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Review review = new Review(
                        5,
                        "최고의 멘토링이었습니다.",
                        reservations.get(i),
                        mentee
                );
                reviews.add(review);
            }
            reviewRepository.saveAll(reviews);

            //when
            MentoringSummaryPaginationResponse firstResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-reservation-count-category-first",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "RESERVATION_COUNT")
                    .queryParam("categoryIds", "1, 2")
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);

            String nextCursorCode = firstResponse.nextCursorCode();
            MentoringSummaryPaginationResponse nextResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-reservation-count-category-next",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "RESERVATION_COUNT")
                    .queryParam("categoryIds", "1, 2")
                    .queryParam("cursorCode", nextCursorCode)
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);
            //then
            SoftAssertions.assertSoftly(softAssertions -> {
                assertThat(firstResponse.mentoringSummaryResponses()).hasSize(10);   // 카테고리 1, 2번 동시 보유 조건
                assertThat(firstResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(10L);
                assertThat(firstResponse.nextCursorCode()).isNotNull();
                assertThat(firstResponse.hasNext()).isTrue();
                assertThat(nextResponse.mentoringSummaryResponses()).hasSize(2);
                assertThat(nextResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(12L);
                assertThat(nextResponse.nextCursorCode()).isNull();
                assertThat(nextResponse.hasNext()).isFalse();
            });
        }

        @DisplayName("첫 멘토링 목록 페이지를 별점 평균 높은 순으로 조회하고, 반환된 커서를 사용하면 나머지 목록을 조회할 수 있다.")
        @Test
        void getMentoringSummaryPages3() {
            //given
            Member mentee = memberRepository.save(
                    new Member("menteeId", Gender.MALE, "멘티1", new Phone("010-1231-1231"), Password.from("pw")));

            List<String> phoneNumbers = List.of(
                    "010-1234-5678",
                    "010-2345-6789",
                    "010-3456-7890",
                    "010-4567-8901",
                    "010-5678-9012",
                    "010-6789-0123",
                    "010-7890-1234",
                    "010-8901-2345",
                    "010-9012-3456",
                    "010-1122-3344",
                    "010-2233-4455",
                    "010-3344-5566"
            );
            List<Member> savedMentor = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Member mentor = new Member(
                        "mentorId" + i,
                        Gender.MALE,
                        "멘토" + i,
                        new Phone(phoneNumbers.get(i)),
                        Password.from("pw"));
                savedMentor.add(mentor);
            }
            memberRepository.saveAll(savedMentor);

            List<Mentoring> savedMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Mentoring mentoring =
                        new Mentoring(
                                savedMentor.get(i),
                                1000,
                                3,
                                "멘토링 내용: " + i,
                                "멘토링 자기소개"
                        );
                savedMentorings.add(mentoring);
            }
            mentoringRepository.saveAll(savedMentorings);

            List<MentoringStatistics> savedMentoringStatistics = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                MentoringStatistics mentoringStatistics = MentoringStatistics.defaultOf(savedMentorings.get(i));
                savedMentoringStatistics.add(mentoringStatistics);
            }
            mentoringStatisticsRepository.saveAll(savedMentoringStatistics);

            Category savedCategory = categoryRepository.save(new Category("체형교정"));

            List<CategoryMentoring> categoryMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                CategoryMentoring categoryMentoring = new CategoryMentoring(savedCategory, savedMentorings.get(i));
                categoryMentorings.add(categoryMentoring);
            }
            categoryMentoringRepository.saveAll(categoryMentorings);

            List<Image> images = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Image image = new Image(
                        "image1.jpg",
                        ImageType.MENTORING_PROFILE,
                        savedMentorings.get(i).getId(),
                        null
                );
                images.add(image);
            }
            imageRepository.saveAll(images);

            List<Reservation> reservations = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Reservation reservation = new Reservation("content", Status.COMPLETE, savedMentorings.get(i), mentee);
                reservations.add(reservation);
                mentoringStatisticsRepository.updateReservationCountPlus(savedMentorings.get(i).getId());
            }
            for (int i = 0; i < 12; i++) {
                Reservation reservation = new Reservation("content", Status.COMPLETE, savedMentorings.get(i), mentee);
                reservations.add(reservation);
                mentoringStatisticsRepository.updateReservationCountPlus(savedMentorings.get(i).getId());
            }
            reservationRepository.saveAll(reservations);

            List<Review> reviews = new ArrayList<>();
            int[] ratingForReviews = {2, 2, 3, 3, 4, 4, 1, 1, 5, 5, 3, 4};
            for (int i = 0; i < ratingForReviews.length; i++) {
                reviews.add(new Review(ratingForReviews[i], "좋았습니다.", reservations.get(i), mentee));
                mentoringStatisticsRepository.updateReviewStatisticsPlus(reservations.get(i).getMentoring().getId(),
                        ratingForReviews[i]);
            }
            for (int i = 0; i < ratingForReviews.length; i++) {
                reviews.add(new Review(ratingForReviews[i], "좋았습니다.", reservations.get(12 + i), mentee));
                mentoringStatisticsRepository.updateReviewStatisticsPlus(
                        reservations.get(12 + i).getMentoring().getId(), ratingForReviews[i]);
            }
            reviewRepository.saveAll(reviews);

            //when
            MentoringSummaryPaginationResponse firstResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-average-rating-first",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "AVERAGE_RATING")
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);

            String nextCursorCode = firstResponse.nextCursorCode();
            MentoringSummaryPaginationResponse nextResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-average-rating-next",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "AVERAGE_RATING")
                    .queryParam("cursorCode", nextCursorCode)
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);

            //then
            SoftAssertions.assertSoftly(softAssertions -> {
                assertThat(firstResponse.mentoringSummaryResponses()).hasSize(10);
                assertThat(firstResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(1L);
                assertThat(firstResponse.nextCursorCode()).isNotNull();
                assertThat(firstResponse.hasNext()).isTrue();
                assertThat(nextResponse.mentoringSummaryResponses()).hasSize(2);
                assertThat(nextResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(7L);
                assertThat(nextResponse.nextCursorCode()).isNull();
                assertThat(nextResponse.hasNext()).isFalse();
            });
        }

        @DisplayName("첫 멘토링 목록 페이지를 별점 평균 높은 순으로 조회하고, 카테고리 필터링을 할 수 있다.")
        @Test
        void getMentoringSummaryPagesWithCategory3() {
            //given
            Member mentee = memberRepository.save(
                    new Member("menteeId", Gender.MALE, "멘티1", new Phone("010-1231-1231"), Password.from("pw")));

            List<String> phoneNumbers = List.of(
                    "111-1234-5678",
                    "111-2345-6789",
                    "111-3456-7890",
                    "111-4567-8901",
                    "111-5678-9012",
                    "111-6789-0123",
                    "111-7890-1234",
                    "111-8901-2345",
                    "111-9012-3456",
                    "111-1122-3344",
                    "111-2233-4455",
                    "111-3344-5566"
            );
            List<Member> savedMentor = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Member mentor = new Member(
                        "mentorId" + i,
                        Gender.MALE,
                        "멘토" + i,
                        new Phone(phoneNumbers.get(i)),
                        Password.from("pw"));
                savedMentor.add(mentor);
            }
            memberRepository.saveAll(savedMentor);

            List<Mentoring> savedMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Mentoring mentoring =
                        new Mentoring(
                                savedMentor.get(i),
                                1000,
                                3,
                                "멘토링 내용: " + i,
                                "멘토링 자기소개"
                        );
                savedMentorings.add(mentoring);
            }
            mentoringRepository.saveAll(savedMentorings);

            List<MentoringStatistics> savedMentoringStatistics = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                MentoringStatistics mentoringStatistics = MentoringStatistics.defaultOf(savedMentorings.get(i));
                savedMentoringStatistics.add(mentoringStatistics);
            }
            mentoringStatisticsRepository.saveAll(savedMentoringStatistics);

            Category category1 = categoryRepository.save(new Category("체형교정"));
            Category category2 = categoryRepository.save(new Category("다이어트"));

            List<CategoryMentoring> categoryMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                CategoryMentoring categoryMentoring1 = new CategoryMentoring(category1, savedMentorings.get(i));
                categoryMentorings.add(categoryMentoring1);
                CategoryMentoring categoryMentoring2 = new CategoryMentoring(category2, savedMentorings.get(i));
                categoryMentorings.add(categoryMentoring2);
            }
            categoryMentoringRepository.saveAll(categoryMentorings);

            List<Image> images = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Image image = new Image(
                        "image1.jpg",
                        ImageType.MENTORING_PROFILE,
                        savedMentorings.get(i).getId(),
                        null
                );
                images.add(image);
            }
            imageRepository.saveAll(images);

            List<Reservation> reservations = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Reservation reservation = new Reservation("content", Status.COMPLETE, savedMentorings.get(i), mentee);
                reservations.add(reservation);
                mentoringStatisticsRepository.updateReservationCountPlus(savedMentorings.get(i).getId());
            }
            for (int i = 0; i < 12; i++) {
                Reservation reservation = new Reservation("content", Status.COMPLETE, savedMentorings.get(i), mentee);
                reservations.add(reservation);
                mentoringStatisticsRepository.updateReservationCountPlus(savedMentorings.get(i).getId());
            }
            reservationRepository.saveAll(reservations);

            List<Review> reviews = new ArrayList<>();
            int[] ratingForReviews = {2, 2, 3, 3, 4, 4, 1, 1, 5, 5, 3, 4};
            for (int i = 0; i < ratingForReviews.length; i++) {
                reviews.add(new Review(ratingForReviews[i], "좋았습니다.", reservations.get(i), mentee));
                mentoringStatisticsRepository.updateReviewStatisticsPlus(reservations.get(i).getMentoring().getId(),
                        ratingForReviews[i]);
            }
            for (int i = 0; i < ratingForReviews.length; i++) {
                reviews.add(new Review(ratingForReviews[i], "좋았습니다.", reservations.get(12 + i), mentee));
                mentoringStatisticsRepository.updateReviewStatisticsPlus(
                        reservations.get(12 + i).getMentoring().getId(), ratingForReviews[i]);
            }
            reviewRepository.saveAll(reviews);

            //when
            MentoringSummaryPaginationResponse firstResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-average-rating-category-first",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "AVERAGE_RATING")
                    .queryParam("categoryIds", "1, 2")
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);

            String nextCursorCode = firstResponse.nextCursorCode();
            MentoringSummaryPaginationResponse nextResponse = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-average-rating-category-next",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .responseSchema(Schema.schema("MentoringSummaryPaginationResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "AVERAGE_RATING")
                    .queryParam("categoryIds", "1, 2")
                    .queryParam("cursorCode", nextCursorCode)
                    .when()
                    .get("/mentorings-page")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MentoringSummaryPaginationResponse.class);

            //then
            SoftAssertions.assertSoftly(softAssertions -> {
                assertThat(firstResponse.mentoringSummaryResponses()).hasSize(10);   // 카테고리 1, 2번 동시 보유 조건
                assertThat(firstResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(1L);
                assertThat(firstResponse.nextCursorCode()).isNotNull();
                assertThat(firstResponse.hasNext()).isTrue();
                assertThat(nextResponse.mentoringSummaryResponses()).hasSize(2);
                assertThat(nextResponse.mentoringSummaryResponses().getLast().id()).isEqualTo(7L);
                assertThat(nextResponse.nextCursorCode()).isNull();
                assertThat(nextResponse.hasNext()).isFalse();
            });
        }

        @DisplayName("잘못된 커서 코드로 조회하면 400 Bad Request를 반환한다")
        @Test
        void getMentoringSummaryPagesFail_invalidCursor() {
            //given
            Member mentee = memberRepository.save(
                    new Member("menteeId2", Gender.MALE, "멘티2", new Phone("010-9999-9999"), Password.from("pw")));
            String invalidCursorCode = "invalid-cursor";

            //when
            Response response = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-fail-invalid-cursor",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .requestSchema(Schema.schema("ErrorResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "CREATED_AT")
                    .queryParam("cursorCode", invalidCursorCode)
                    .when()
                    .get("/mentorings-page");

            //then
            String responseMessage = response.jsonPath().getString("message");
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(400);
                softly.assertThat(responseMessage).isEqualTo("Invalid cursor");
            });
        }

        @DisplayName("존재하지 않는 sortKey로 조회하면 400 Bad Request를 반환한다")
        @Test
        void getMentoringSummaryPagesFail_invalidSortKey() {
            //when
            Response response = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-fail-invalid-sortkey",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("멘토링")
                                    .requestSchema(Schema.schema("ErrorResponse"))
                                    .build())))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "INVALID_SORT_KEY")
                    .when()
                    .get("/mentorings-page");

            //then
            assertThat(response.statusCode()).isEqualTo(400);
        }
    }
}
