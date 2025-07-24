package fittoring.mentoring.presentation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.repository.CategoryMentoringRepository;
import fittoring.mentoring.business.repository.CategoryRepository;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.presentation.dto.MentoringResponse;
import fittoring.util.DbCleaner;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MentoringControllerTest {

    @LocalServerPort
    public int port;

    @Autowired
    private CategoryMentoringRepository categoryMentoringRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        dbCleaner.clean();
    }

    @DisplayName("멘토링 목록 조회 API 테스트")
    @Nested
    class FindMentoring {

        @DisplayName("필터링 없이 모든 멘토링 목록 조회에 성공하면, 200 OK 상태코드와 멘토링 목록을 반환한다.")
        @Test
        void getAllCategories() {
            //given
            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            "멘토링1",
                            "010-1234-5678",
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );

            Mentoring savedMentoring2 = mentoringRepository.save(
                    new Mentoring(
                            "멘토링2",
                            "010-1111-2222",
                            1000,
                            4,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );

            CategoryMentoring savedCategoryMentoring = categoryMentoringRepository.save(
                    new CategoryMentoring(savedCategory, savedMentoring)
            );
            CategoryMentoring savedCategoryMentoring2 = categoryMentoringRepository.save(
                    new CategoryMentoring(savedCategory2, savedMentoring2)
            );

            Image savedImage = imageRepository.save(
                    new Image("image1.jpg", ImageType.MENTORING, savedMentoring.getId())
            );
            Image savedImage2 = imageRepository.save(
                    new Image("image2.jpg", ImageType.MENTORING, savedMentoring2.getId())
            );

            //when
            List<MentoringResponse> response = RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .when()
                    .get("/mentorings")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<>() {
                    });

            //then
            MentoringResponse expected = new MentoringResponse(
                    savedMentoring.getId(),
                    savedMentoring.getMentorName(),
                    List.of(savedCategoryMentoring.getCategoryTitle()),
                    savedMentoring.getPrice(),
                    savedMentoring.getCareer(),
                    savedImage.getUrl(),
                    savedMentoring.getIntroduction()
            );

            MentoringResponse expected2 = new MentoringResponse(
                    savedMentoring2.getId(),
                    savedMentoring2.getMentorName(),
                    List.of(savedCategoryMentoring2.getCategoryTitle()),
                    savedMentoring2.getPrice(),
                    savedMentoring2.getCareer(),
                    savedImage2.getUrl(),
                    savedMentoring2.getIntroduction()
            );

            assertThat(response)
                    .isNotNull()
                    .containsExactlyInAnyOrder(expected, expected2);
        }

        @DisplayName("조회할 멘토링 목록이 없을 때, 200 OK 상태코드와 멘토링 목록을 반환한다.")
        @Test
        void getAllCategories2() {
            //given
            //when
            List<MentoringResponse> response = RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
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
            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));
            Category savedCategory3 = categoryRepository.save(new Category("영양식단"));

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            "멘토링1",
                            "010-1234-5678",
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );

            Mentoring savedMentoring2 = mentoringRepository.save(
                    new Mentoring(
                            "멘토링2",
                            "010-1111-2222",
                            1000,
                            4,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );

            Mentoring savedMentoring3 = mentoringRepository.save(
                    new Mentoring(
                            "멘토링3",
                            "010-2222-3333",
                            1001,
                            5,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );

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
                    new Image("image1.jpg", ImageType.MENTORING, savedMentoring.getId())
            );
            Image savedImage2 = imageRepository.save(
                    new Image("image2.jpg", ImageType.MENTORING, savedMentoring2.getId())
            );
            Image savedImage3 = imageRepository.save(
                    new Image("image3.jpg", ImageType.MENTORING, savedMentoring3.getId())
            );

            //when
            List<MentoringResponse> response = RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
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
            MentoringResponse expected = new MentoringResponse(
                    savedMentoring.getId(),
                    savedMentoring.getMentorName(),
                    List.of(
                            savedCategoryMentoring.getCategoryTitle(),
                            savedCategoryMentoring2_1.getCategoryTitle()
                    ),
                    savedMentoring.getPrice(),
                    savedMentoring.getCareer(),
                    savedImage.getUrl(),
                    savedMentoring.getIntroduction()
            );

            MentoringResponse expected2 = new MentoringResponse(
                    savedMentoring2.getId(),
                    savedMentoring2.getMentorName(),
                    List.of(
                            savedCategoryMentoring1_2.getCategoryTitle(),
                            savedCategoryMentoring2_2.getCategoryTitle()
                    ),
                    savedMentoring2.getPrice(),
                    savedMentoring2.getCareer(),
                    savedImage2.getUrl(),
                    savedMentoring2.getIntroduction()
            );

            MentoringResponse expected3 = new MentoringResponse(
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
                    savedMentoring3.getIntroduction()
            );
            assertThat(response)
                    .isNotNull()
                    .containsExactlyInAnyOrder(expected, expected2, expected3);
        }

        @DisplayName("필터 조건에 해당하는 멘토링이 존재하지 않는 경우 200 OK 상태코드와 빈 멘토링 목록을 반환한다.")
        @Test
        void getAllCategories4() {
            //given
            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));
            Category savedCategory3 = categoryRepository.save(new Category("영양식단"));
            Category savedCategory4 = categoryRepository.save(new Category("건강관리"));
            Category savedCategory5 = categoryRepository.save(new Category("민간요법"));

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            "멘토링1",
                            "010-1234-5678",
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );

            Mentoring savedMentoring2 = mentoringRepository.save(
                    new Mentoring(
                            "멘토링2",
                            "010-1111-2222",
                            1000,
                            4,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );

            Mentoring savedMentoring3 = mentoringRepository.save(
                    new Mentoring(
                            "멘토링3",
                            "010-2222-3333",
                            1001,
                            5,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );

            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring2));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring2));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring3));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring3));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory3, savedMentoring3));

            imageRepository.save(new Image("image1.jpg", ImageType.MENTORING, savedMentoring.getId()));
            imageRepository.save(new Image("image2.jpg", ImageType.MENTORING, savedMentoring2.getId()));
            imageRepository.save(new Image("image3.jpg", ImageType.MENTORING, savedMentoring3.getId()));

            //when
            List<MentoringResponse> response = RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
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

        // 전역 예외처리 도입 후 테스트 활성화 필요(@Disabled 어노테이션 제거)
        @Disabled
        @DisplayName("존재하지 않는 카테고리로 필터링을 시도할 때, 400 Bad Request 상태코드를 반환한다.")
        @Test
        void getAllCategories5() {
            //given
            Category savedCategory = categoryRepository.save(new Category("체형교정"));
            Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

            Mentoring savedMentoring = mentoringRepository.save(
                    new Mentoring(
                            "멘토링1",
                            "010-1234-5678",
                            1000,
                            3,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );

            Mentoring savedMentoring2 = mentoringRepository.save(
                    new Mentoring(
                            "멘토링2",
                            "010-1111-2222",
                            1000,
                            4,
                            "멘토링 내용",
                            "멘토링 자기소개"
                    )
            );

            categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));
            categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring2));

            imageRepository.save(new Image("image1.jpg", ImageType.MENTORING, savedMentoring.getId()));
            imageRepository.save(new Image("image2.jpg", ImageType.MENTORING, savedMentoring2.getId()));

            //when
            Response response = RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .queryParam("categoryTitle1", "존재하지 않는 카테고리")
                    .queryParam("categoryTitle2", savedCategory.getTitle())
                    .when()
                    .get("/mentorings");

            //then
            String responseMessage = response.jsonPath().getString("message");
            assertSoftly(softly -> {
                softly.assertThat(response).isNotNull();
                softly.assertThat(response.statusCode()).isEqualTo(400);
                softly.assertThat(responseMessage).isEqualTo("존재하지 않는 카테고리가 포함되어 있습니다.");
            });
        }
    }

    @DisplayName("멘토링 Id로 멘토링 조회에 성공하면 200 OK 상태코드와 멘토링 정보를 반환한다.")
    @Test
    void getMentoring() {
        //given
        Category savedCategory = categoryRepository.save(new Category("체형교정"));
        Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

        Mentoring savedMentoring = mentoringRepository.save(
                new Mentoring(
                        "멘토링1",
                        "010-1234-5678",
                        1000,
                        3,
                        "멘토링 내용",
                        "멘토링 자기소개"
                )
        );

        Mentoring savedMentoring2 = mentoringRepository.save(
                new Mentoring(
                        "멘토링2",
                        "010-1111-2222",
                        1000,
                        4,
                        "멘토링 내용",
                        "멘토링 자기소개"
                )
        );

        categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));
        categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring2));

        imageRepository.save(new Image("image1.jpg", ImageType.MENTORING, savedMentoring.getId()));
        imageRepository.save(new Image("image2.jpg", ImageType.MENTORING, savedMentoring2.getId()));

        Long mentoringId = savedMentoring.getId();

        //when
        MentoringResponse response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .queryParam("categoryTitle1", "존재하지 않는 카테고리")
                .queryParam("categoryTitle2", savedCategory.getTitle())
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
                savedMentoring.getIntroduction()
        );
        assertThat(response).isNotNull().isEqualTo(expected);
    }

    // 전역 예외처리 도입 후 테스트 활성화 필요(@Disabled 어노테이션 제거)
    @Disabled
    @DisplayName("존재하지 않는 멘토링 Id로 멘토링 조회에 실패하면 404 Not Found 상태코드를 반환한다.")
    @Test
    void getMentoring2() {
        //given
        Category savedCategory = categoryRepository.save(new Category("체형교정"));
        Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

        Mentoring savedMentoring = mentoringRepository.save(
                new Mentoring(
                        "멘토링1",
                        "010-1234-5678",
                        1000,
                        3,
                        "멘토링 내용",
                        "멘토링 자기소개"
                )
        );

        Mentoring savedMentoring2 = mentoringRepository.save(
                new Mentoring(
                        "멘토링2",
                        "010-1111-2222",
                        1000,
                        4,
                        "멘토링 내용",
                        "멘토링 자기소개"
                )
        );

        categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));
        categoryMentoringRepository.save(new CategoryMentoring(savedCategory2, savedMentoring2));

        imageRepository.save(new Image("image1.jpg", ImageType.MENTORING, savedMentoring.getId()));
        imageRepository.save(new Image("image2.jpg", ImageType.MENTORING, savedMentoring2.getId()));

        Long mentoringId = 100L;

        //when
        Response response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .queryParam("categoryTitle1", "존재하지 않는 카테고리")
                .queryParam("categoryTitle2", savedCategory.getTitle())
                .when()
                .get("/mentorings/" + mentoringId);

        //then
        String responseMessage = response.jsonPath().getString("message");
        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(404);
            softly.assertThat(responseMessage).isEqualTo("해당하는 멘토링을 찾을 수 없습니다. ID : " + mentoringId);
        });
    }

}


