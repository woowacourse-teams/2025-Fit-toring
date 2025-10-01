package fittoring.integration.mentoring.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.CertificateType;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Review;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.CategoryMentoringRepository;
import fittoring.mentoring.business.repository.CategoryRepository;
import fittoring.mentoring.business.repository.CertificateRepository;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.repository.ReviewRepository;
import fittoring.mentoring.business.service.JwtProvider;
import fittoring.mentoring.business.service.PresignedUrlService;
import fittoring.mentoring.business.service.dto.MentoringSummaryPaginationResponse;
import fittoring.mentoring.business.service.dto.RatingStatsDto;
import fittoring.mentoring.presentation.dto.CertificateSpecAndImageResponse;
import fittoring.mentoring.presentation.dto.MentoringRegisterRequest;
import fittoring.mentoring.presentation.dto.MentoringResponse;
import fittoring.mentoring.presentation.dto.MentoringSummaryResponse;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class MentoringControllerTest extends AbstractApiDocumentationTest {

    @MockitoBean
    private PresignedUrlService presignedUrlService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

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

    @DisplayName("개설된 멘토링을 수정 성공하면 200 OK를 반환한다")
    @Test
    void modifyMentoring() throws IOException {
        //given
        Member mentor = memberRepository.save(new Member(
                "id1",
                "MALE",
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
                "긴 글 소개",
                "가상의카카오오픈채팅"
        ));
        imageRepository.save(new Image(
                "originalProfileImage",
                ImageType.MENTORING_PROFILE,
                mentoring.getId()
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
                certificate.getId()
        ));

        int newPrice = 1000;
        String newCategory = "category2";
        String newIntroduction = "수정된 긴 글 소개";
        String newProfileImageUrl = "수정된 새로운 프로필 이미지";
        int newCareer = 5;
        String newContent = "수정된 한 줄 소개";
        String chatUrl = "가상의카카오오픈채팅";
        MentoringRegisterRequest requestBody = new MentoringRegisterRequest(
                newPrice,
                List.of(newCategory),
                newIntroduction,
                newProfileImageUrl,
                newCareer,
                newContent,
                chatUrl,
                Collections.emptyList()
        );
        String accessToken = jwtProvider.createAccessToken(mentor.getId());

        given(presignedUrlService.isObjectExistsFromKey(anyString()))
                .willReturn(true);

        // when
        // then
        RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(requestBody))
                .when()
                .put("/mentorings/" + mentoring.getId())
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("존재하지 않는 멘토링을 수정하려고 하면 404 Not Found를 반환한다")
    @Test
    void modifyMentoringFail1() throws IOException {
        // given
        Member mentor = memberRepository.save(new Member(
                "id1",
                "MALE",
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
        String chatUrl = "가상의카카오오픈채팅";
        MentoringRegisterRequest requestBody = new MentoringRegisterRequest(
                newPrice,
                List.of(newCategory),
                newIntroduction,
                newProfileImageUrl,
                newCareer,
                newContent,
                chatUrl,
                Collections.emptyList()
        );
        String accessToken = jwtProvider.createAccessToken(mentor.getId());

        // when
        // then
        RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(requestBody))
                .when()
                .put("/mentorings/999")
                .then().log().all()
                .statusCode(404);
    }

    @DisplayName("본인이 개설하지 않은 멘토링을 수정하려고 하면 403 Forbidden를 반환한다")
    @Test
    void modifyMentoringFail2() throws IOException {
        // given
        Member mentor = memberRepository.save(new Member(
                "id1",
                "MALE",
                "김트레이너",
                new Phone("010-1234-9048"),
                Password.from("pw")
        ));
        String chatUrl = "가상의카카오오픈채팅링크";
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                3,
                "한 줄 소개",
                "긴 글 소개",
                chatUrl
        ));

        Member invalidMember = memberRepository.save(new Member(
                "id2",
                "MALE",
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
                chatUrl,
                Collections.emptyList()
        );
        String accessToken = jwtProvider.createAccessToken(invalidMember.getId());

        // when
        // then
        RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(requestBody))
                .when()
                .put("/mentorings/" + mentoring.getId())
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("멘토링 목록 조회 API 테스트")
    @Nested
    class FindMentoring {

        @DisplayName("필터링 없이 모든 멘토링 목록 조회에 성공하면, 200 OK 상태코드와 멘토링 목록을 반환한다.")
        @Test
        void getAllCategories() {
            //given
            Member mentee = memberRepository.save(
                    new Member("id", "MALE", "멘티1", new Phone("010-1231-1231"), Password.from("pw")));
            String accessToken = jwtProvider.createAccessToken(mentee.getId());

            Member mentor1 = memberRepository.save(
                    new Member("id1", "MALE", "멘토1", new Phone("010-1234-5678"), Password.from("pw")));
            Member mentor2 = memberRepository.save(
                    new Member("id2", "MALE", "멘토2", new Phone("010-1111-2222"), Password.from("pw")));

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            mentor1,
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"

                    )
            );

            Mentoring savedMentoring2 = mentoringRepository.save(
                    new Mentoring(
                            mentor2,
                            1000,
                            4,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

            CategoryMentoring savedCategoryMentoring = categoryMentoringRepository.save(
                    new CategoryMentoring(savedCategory, savedMentoring)
            );
            CategoryMentoring savedCategoryMentoring2 = categoryMentoringRepository.save(
                    new CategoryMentoring(savedCategory2, savedMentoring2)
            );

            Image savedImage = imageRepository.save(
                    new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentoring.getId())
            );
            Image savedImage2 = imageRepository.save(
                    new Image("image2.jpg", ImageType.MENTORING_PROFILE, savedMentoring2.getId())
            );

            //예약 생성
            Reservation savedReservation = reservationRepository.save(
                    new Reservation("content", Status.COMPLETE, savedMentoring, mentee));

            Reservation savedReservation2 = reservationRepository.save(
                    new Reservation("content", Status.COMPLETE, savedMentoring2, mentee));

            //리뷰 생성
            Review review = new Review(
                    5,
                    "최고의 멘토링이었습니다.",
                    savedReservation,
                    mentee
            );
            reviewRepository.save(review);

            Review review2 = new Review(
                    2,
                    "별로의 멘토링이었습니다.",
                    savedReservation2,
                    mentee
            );
            reviewRepository.save(review2);

            //when
            List<MentoringSummaryResponse> response = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-no-filter-success"))
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", accessToken)
                    .when()
                    .get("/mentorings")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<>() {
                    });

            //then
            RatingStatsDto ratingStatsDto = new RatingStatsDto(savedMentoring.getId(), 5.0, 1);
            RatingStatsDto ratingStatsDto2 = new RatingStatsDto(savedMentoring.getId(), 2.0, 1);

            MentoringSummaryResponse expected = new MentoringSummaryResponse(
                    savedMentoring.getId(),
                    savedMentoring.getMentorName(),
                    List.of(savedCategoryMentoring.getCategoryTitle()),
                    savedMentoring.getPrice(),
                    savedMentoring.getCareer(),
                    savedImage.getUrl(),
                    savedMentoring.getIntroduction(),
                    String.format("%.1f", ratingStatsDto.average()),
                    ratingStatsDto.count()
            );

            MentoringSummaryResponse expected2 = new MentoringSummaryResponse(
                    savedMentoring2.getId(),
                    savedMentoring2.getMentorName(),
                    List.of(savedCategoryMentoring2.getCategoryTitle()),
                    savedMentoring2.getPrice(),
                    savedMentoring2.getCareer(),
                    savedImage2.getUrl(),
                    savedMentoring2.getIntroduction(),
                    String.format("%.1f", ratingStatsDto2.average()),
                    ratingStatsDto2.count()
            );

            assertThat(response).containsExactlyInAnyOrder(expected, expected2);
        }

        @DisplayName("조회할 멘토링 목록이 없을 때, 200 OK 상태코드와 멘토링 목록을 반환한다.")
        @Test
        void getAllCategories2() {
            //given
            Member mentee = memberRepository.save(
                    new Member("id", "MALE", "멘티1", new Phone("010-1231-1231"), Password.from("pw")));
            String accessToken = jwtProvider.createAccessToken(mentee.getId());
            //when
            List<MentoringResponse> response = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-empty-success"))
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", accessToken)
                    .when()
                    .get("/mentorings")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<>() {
                    });

            //then
            assertThat(response).isNotNull().isEmpty();
        }

        @DisplayName("필터링을 통해서 멘토링 목록을 조회할 때, 요청의 카테고리를 모두 포함하고, 추가적인 카테고리를 포함하는 멘토링 조회에 성공하면 200 OK 상태코드와 필터링된 멘토링 목록을 반환한다.")
        @Test
        void getAllCategories3() {
            //given
            Member mentee = memberRepository.save(
                    new Member("id", "MALE", "멘티1", new Phone("010-1231-1231"), Password.from("pw")));
            String accessToken = jwtProvider.createAccessToken(mentee.getId());
            Member mentor1 = memberRepository.save(
                    new Member("id1", "MALE", "멘토1", new Phone("010-1234-5678"), Password.from("pw")));
            Member mentor2 = memberRepository.save(
                    new Member("id2", "MALE", "멘토2", new Phone("010-1111-2222"), Password.from("pw")));
            Member mentor3 = memberRepository.save(
                    new Member("id3", "MALE", "멘토3", new Phone("010-2222-3333"), Password.from("pw")));

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            mentor1,
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Mentoring savedMentoring2 = mentoringRepository.save(
                    new Mentoring(
                            mentor2,
                            1000,
                            4,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Mentoring savedMentoring3 = mentoringRepository.save(
                    new Mentoring(
                            mentor3,
                            1000,
                            5,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));
            Category savedCategory3 = categoryRepository.save(new Category("영양식단"));

            CategoryMentoring savedCategoryMentoring = categoryMentoringRepository.save(
                    new CategoryMentoring(savedCategory, savedMentoring)
            );
            CategoryMentoring savedCategoryMentoring2_1 = categoryMentoringRepository.save(
                    new CategoryMentoring(savedCategory2, savedMentoring)
            );
            CategoryMentoring savedCategoryMentoring1_2 = categoryMentoringRepository.save(
                    new CategoryMentoring(savedCategory, savedMentoring2)
            );
            CategoryMentoring savedCategoryMentoring2_2 = categoryMentoringRepository.save(
                    new CategoryMentoring(savedCategory2, savedMentoring2)
            );
            CategoryMentoring savedCategoryMentoring1_3 = categoryMentoringRepository.save(
                    new CategoryMentoring(savedCategory, savedMentoring3)
            );
            CategoryMentoring savedCategoryMentoring2_3 = categoryMentoringRepository.save(
                    new CategoryMentoring(savedCategory2, savedMentoring3)
            );
            CategoryMentoring savedCategoryMentoring3_3 = categoryMentoringRepository.save(
                    new CategoryMentoring(savedCategory3, savedMentoring3)
            );

            Image savedImage = imageRepository.save(
                    new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentoring.getId())
            );
            Image savedImage2 = imageRepository.save(
                    new Image("image2.jpg", ImageType.MENTORING_PROFILE, savedMentoring2.getId())
            );
            Image savedImage3 = imageRepository.save(
                    new Image("image3.jpg", ImageType.MENTORING_PROFILE, savedMentoring3.getId())
            );

            //when
            List<MentoringSummaryResponse> response = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-with-filter-success"))
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", accessToken)
                    .queryParam("categoryTitle1", savedCategory.getTitle())
                    .queryParam("categoryTitle2", savedCategory2.getTitle())
                    .when()
                    .get("/mentorings")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<>() {
                    });

            //then
            MentoringSummaryResponse expected = new MentoringSummaryResponse(
                    savedMentoring.getId(),
                    savedMentoring.getMentorName(),
                    List.of(
                            savedCategoryMentoring.getCategoryTitle(),
                            savedCategoryMentoring2_1.getCategoryTitle()
                    ),
                    savedMentoring.getPrice(),
                    savedMentoring.getCareer(),
                    savedImage.getUrl(),
                    savedMentoring.getIntroduction(),
                    String.format("%.1f", 0.0),
                    0
            );

            MentoringSummaryResponse expected2 = new MentoringSummaryResponse(
                    savedMentoring2.getId(),
                    savedMentoring2.getMentorName(),
                    List.of(
                            savedCategoryMentoring1_2.getCategoryTitle(),
                            savedCategoryMentoring2_2.getCategoryTitle()
                    ),
                    savedMentoring2.getPrice(),
                    savedMentoring2.getCareer(),
                    savedImage2.getUrl(),
                    savedMentoring2.getIntroduction(),
                    String.format("%.1f", 0.0),
                    0
            );

            MentoringSummaryResponse expected3 = new MentoringSummaryResponse(
                    savedMentoring3.getId(),
                    savedMentoring3.getMentorName(),
                    List.of(
                            savedCategoryMentoring1_3.getCategoryTitle(),
                            savedCategoryMentoring2_3.getCategoryTitle(),
                            savedCategoryMentoring3_3.getCategoryTitle()
                    ),
                    savedMentoring3.getPrice(),
                    savedMentoring3.getCareer(),
                    savedImage3.getUrl(),
                    savedMentoring3.getIntroduction(),
                    String.format("%.1f", 0.0),
                    0
            );

            List<MentoringSummaryResponse> expectedList = List.of(expected, expected2, expected3);

            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedList);
        }

        @DisplayName("필터 조건에 해당하는 멘토링이 존재하지 않는 경우 200 OK 상태코드와 빈 멘토링 목록을 반환한다.")
        @Test
        void getAllCategories4() {
            //given
            Member mentee = memberRepository.save(
                    new Member("id", "MALE", "멘티1", new Phone("010-1231-1231"), Password.from("pw")));
            String accessToken = jwtProvider.createAccessToken(mentee.getId());

            Member mentor1 = memberRepository.save(
                    new Member("id1", "MALE", "멘토1", new Phone("010-1234-5678"), Password.from("pw")));
            Member mentor2 = memberRepository.save(
                    new Member("id2", "MALE", "멘토2", new Phone("010-1111-2222"), Password.from("pw")));
            Member mentor3 = memberRepository.save(
                    new Member("id3", "MALE", "멘토3", new Phone("010-2222-3333"), Password.from("pw")));

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            mentor1,
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Mentoring savedMentoring2 = mentoringRepository.save(
                    new Mentoring(
                            mentor2,
                            1000,
                            4,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Mentoring savedMentoring3 = mentoringRepository.save(
                    new Mentoring(
                            mentor3,
                            1001,
                            5,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));
            Category savedCategory3 = categoryRepository.save(new Category("영양식단"));
            Category savedCategory4 = categoryRepository.save(new Category("건강관리"));
            Category savedCategory5 = categoryRepository.save(new Category("민간요법"));

            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring2));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring2));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring3));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring3));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory3, savedMentoring3));

            imageRepository.save(new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentoring.getId()));
            imageRepository.save(new Image("image2.jpg", ImageType.MENTORING_PROFILE, savedMentoring2.getId()));
            imageRepository.save(new Image("image3.jpg", ImageType.MENTORING_PROFILE, savedMentoring3.getId()));

            //when
            List<MentoringResponse> response = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-with-filter-empty-success"))
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", accessToken)
                    .queryParam("categoryTitle1", savedCategory4.getTitle())
                    .queryParam("categoryTitle2", savedCategory5.getTitle())
                    .when()
                    .get("/mentorings")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<>() {
                    });

            //then
            assertThat(response).isNotNull().isEmpty();
        }

        @DisplayName("존재하지 않는 카테고리로 필터링을 시도할 때, 404 NotFound 상태코드를 반환한다.")
        @Test
        void getAllCategories5() {
            //given
            Member mentee = memberRepository.save(
                    new Member("id", "MALE", "멘티1", new Phone("010-1231-1231"), Password.from("pw")));
            String accessToken = jwtProvider.createAccessToken(mentee.getId());

            Member mentor1 = memberRepository.save(
                    new Member("id1", "MALE", "멘토1", new Phone("010-1234-5678"), Password.from("pw")));
            Member mentor2 = memberRepository.save(
                    new Member("id2", "MALE", "멘토2", new Phone("010-1111-2222"), Password.from("pw")));

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            mentor1,
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Mentoring savedMentoring2 = mentoringRepository.save(
                    new Mentoring(
                            mentor2,
                            1000,
                            4,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring2));

            imageRepository.save(new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentoring.getId()));
            imageRepository.save(new Image("image2.jpg", ImageType.MENTORING_PROFILE, savedMentoring2.getId()));

            //when
            Response response = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-category-not-found"))
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", accessToken)
                    .queryParam("categoryTitle1", "존재하지 않는 카테고리")
                    .queryParam("categoryTitle2", savedCategory.getTitle())
                    .when()
                    .get("/mentorings");

            //then
            String responseMessage = response.jsonPath().getString("message");
            assertSoftly(softly -> {
                softly.assertThat(response).isNotNull();
                softly.assertThat(response.statusCode()).isEqualTo(404);
                softly.assertThat(responseMessage).isEqualTo(BusinessErrorMessage.CATEGORY_NOT_FOUND.getMessage());
            });
        }

        @DisplayName("멘토링 Id로 멘토링 조회에 성공하면 200 OK 상태코드와 멘토링 정보를 반환한다.")
        @Test
        void getMentoring() {
            //given
            Member mentee = memberRepository.save(
                    new Member("id", "MALE", "멘티1", new Phone("010-1231-1231"), Password.from("pw")));
            String accessToken = jwtProvider.createAccessToken(mentee.getId());

            Member mentor1 = memberRepository.save(
                    new Member("id1", "MALE", "멘토1", new Phone("010-1234-5678"), Password.from("pw")));
            Member mentor2 = memberRepository.save(
                    new Member("id2", "MALE", "멘토2", new Phone("010-1111-2222"), Password.from("pw")));

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            mentor1,
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Mentoring savedMentoring2 = mentoringRepository.save(
                    new Mentoring(
                            mentor2,
                            1000,
                            4,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring2));

            imageRepository.save(new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentoring.getId()));
            imageRepository.save(new Image("image2.jpg", ImageType.MENTORING_PROFILE, savedMentoring2.getId()));

            Reservation savedReservation1 = reservationRepository.save(
                    new Reservation("예약 코멘트1", Status.COMPLETE, savedMentoring, mentee));
            Reservation savedReservation2 = reservationRepository.save(
                    new Reservation("예약 코멘트2", Status.COMPLETE, savedMentoring, mentee));

            reviewRepository.save(new Review(4, "리뷰 코멘트", savedReservation1, mentee));
            reviewRepository.save(new Review(5, "리뷰 코멘트", savedReservation2, mentee));

            Long mentoringId = savedMentoring.getId();

            //when
            MentoringResponse response = RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", accessToken)
                    .queryParam("categoryTitle1", savedCategory.getTitle())
                    .queryParam("categoryTitle2", savedCategory2.getTitle())
                    .when()
                    .get("/mentorings/" + mentoringId)
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
                    savedMentoring.getChatUrl(),
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
            Member mentor = new Member("id1", "MALE", "멘토1", new Phone("010-1234-5678"), Password.from("pw"));
            Member savedMentor = memberRepository.save(mentor);

            //토큰 생성
            String accessToken = jwtProvider.createAccessToken(mentor.getId());

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            savedMentor,
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            //카테고리 생성
            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

            //멘토링 카테고리 생성
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring));

            //멘토링 프로필 이미지 생성
            Image savedImage = imageRepository.save(
                    new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentoring.getId()));

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
                    savedCertificate.getId())
            );

            Image savedCertificateImage2 = imageRepository.save(new Image(
                    "profileImageUrl2",
                    ImageType.CERTIFICATE,
                    savedCertificate2.getId())
            );

            //when
            MentoringResponse response = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-mine-success"))
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
                    new Member("id", "MALE", "멘티1", new Phone("010-1231-1231"), Password.from("pw")));
            String accessToken = jwtProvider.createAccessToken(mentee.getId());

            Member mentor1 = memberRepository.save(
                    new Member("id1", "MALE", "멘토1", new Phone("010-1234-5678"), Password.from("pw")));
            Member mentor2 = memberRepository.save(
                    new Member("id2", "MALE", "멘토2", new Phone("010-1111-2222"), Password.from("pw")));

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            mentor1,
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Mentoring savedMentoring2 = mentoringRepository.save(
                    new Mentoring(
                            mentor2,
                            1000,
                            4,
                            "멘토링 내용",
                            "멘토링 자기소개",
                            "가상의카카오오픈채팅"
                    )
            );

            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring2));

            imageRepository.save(new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentoring.getId()));
            imageRepository.save(new Image("image2.jpg", ImageType.MENTORING_PROFILE, savedMentoring2.getId()));

            long invalidId = 100L;

            //when
            Response response = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-id-not-found"))
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", accessToken)
                    .queryParam("categoryTitle1", savedCategory.getTitle())
                    .queryParam("categoryTitle2", savedCategory.getTitle())
                    .when()
                    .get("/mentorings/" + invalidId);

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
                    new Member("menteeId", "MALE", "멘티1", new Phone("010-1231-1231"), Password.from("pw")));

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
                        "MALE",
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
                                "멘토링 자기소개",
                                "가상의카카오오픈채팅"
                        );
                savedMentorings.add(mentoring);
            }
            mentoringRepository.saveAll(savedMentorings);

            Category savedCategory = categoryRepository.save(new Category("체형교정"));

            List<CategoryMentoring> categoryMentorings = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                CategoryMentoring categoryMentoring = new CategoryMentoring(savedCategory, savedMentorings.get(i));
                categoryMentorings.add(categoryMentoring);
            }
            categoryMentoringRepository.saveAll(categoryMentorings);

            List<Image> images = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Image image = new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentorings.get(i).getId());
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
                    .filter(documentWithTag("mentoring/get-mentorings-page-success-first"))
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
                    .filter(documentWithTag("mentoring/get-mentorings-page-success-next"))
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
                    new Member("menteeId", "MALE", "멘티1", new Phone("010-1231-1231"), Password.from("pw")));

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
                        "MALE",
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
                                "멘토링 자기소개",
                                "가상의카카오오픈채팅"
                        );
                savedMentorings.add(mentoring);
            }
            mentoringRepository.saveAll(savedMentorings);

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
                Image image = new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentorings.get(i).getId());
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
                    .filter(documentWithTag("mentoring/get-mentorings-page-success-first"))
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
                    new Member("menteeId", "MALE", "멘티1", new Phone("010-1231-1231"), Password.from("pw")));

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
                        "MALE",
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
                                "멘토링 자기소개",
                                "가상의카카오오픈채팅"
                        );
                savedMentorings.add(mentoring);
            }
            mentoringRepository.saveAll(savedMentorings);

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
                Image image = new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentorings.get(i).getId());
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
                    .filter(documentWithTag("mentoring/get-mentorings-page-success-first"))
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
                    .filter(documentWithTag("mentoring/get-mentorings-page-success-next"))
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

        @DisplayName("잘못된 커서 코드로 조회하면 400 Bad Request를 반환한다")
        @Test
        void getMentoringSummaryPagesFail_invalidCursor() {
            //given
            Member mentee = memberRepository.save(
                    new Member("menteeId2", "MALE", "멘티2", new Phone("010-9999-9999"), Password.from("pw")));
            String invalidCursorCode = "invalid-cursor";

            //when
            Response response = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("mentoring/get-mentorings-page-fail-invalid-cursor"))
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
                    .filter(documentWithTag("mentoring/get-mentorings-page-fail-invalid-sortkey"))
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("sortKey", "INVALID_SORT_KEY")
                    .when()
                    .get("/mentorings-page");

            //then
            assertThat(response.statusCode()).isEqualTo(400);
        }
    }
}


