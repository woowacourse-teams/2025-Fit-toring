package fittoring.mentoring.presentation.api;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.repository.CategoryRepository;
import fittoring.mentoring.presentation.dto.CategoryResponse;
import fittoring.util.DbCleaner;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.util.List;
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
class CategoryControllerTest {

    @LocalServerPort
    public int port;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        dbCleaner.clean();
    }

    @DisplayName("카테고리 목록 조회가 성공하면, 200 OK 상태고리와 카테고리 목록을 반환한다.")
    @Test
    void getAllCategories() {
        //given
        Category savedCategory1 = categoryRepository.save(new Category("체형교정"));
        Category savedCategory2 = categoryRepository.save(new Category("근육증진"));
        Category savedCategory3 = categoryRepository.save(new Category("영양식단"));

        //when
        List<CategoryResponse> response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .when()
                .get("/categories")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        //then
        CategoryResponse expected = new CategoryResponse(savedCategory1.getId(), savedCategory1.getTitle());
        CategoryResponse expected2 = new CategoryResponse(savedCategory2.getId(), savedCategory2.getTitle());
        CategoryResponse expected3 = new CategoryResponse(savedCategory3.getId(), savedCategory3.getTitle());

        assertThat(response)
                .hasSize(3)
                .contains(expected, expected2, expected3);
    }

    @DisplayName("등록된 카테고리가 없을 때, 200 OK 상태고리와 빈 카테고리 목록을 반환한다.")
    @Test
    void getAllCategories2() {
        //given
        //when
        List<CategoryResponse> response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .when()
                .get("/categories")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        //then
        assertThat(response).isEmpty();
    }
}
