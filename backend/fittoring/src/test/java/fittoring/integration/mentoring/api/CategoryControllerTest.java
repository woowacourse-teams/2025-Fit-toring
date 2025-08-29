package fittoring.integration.mentoring.api;

import com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper;
import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.repository.CategoryRepository;
import fittoring.mentoring.presentation.dto.CategoryResponse;
import fittoring.util.DbCleaner;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentationConfigurer;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {

    private RequestSpecification spec;

    private RestDocumentationFilter documentWithTag(String id) {
        String tag = id.contains("/") ? id.substring(0, id.indexOf('/')) : id;
        return RestAssuredRestDocumentationWrapper.document(id, resource(builder().tag(tag).build()));
    }

    @LocalServerPort
    public int port;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        dbCleaner.clean();
        RestAssuredRestDocumentationConfigurer restAssuredConfig = documentationConfiguration(restDocumentation);
        restAssuredConfig.operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint());
        spec = new RequestSpecBuilder()
                .addFilter(restAssuredConfig)
                .build();
    }

    @DisplayName("카테고리 목록 조회가 성공하면, 200 OK 상태코드와 카테고리 목록을 반환한다.")
    @Test
    void getAllCategories() {
        //given
        Category savedCategory1 = categoryRepository.save(new Category("체형교정"));
        Category savedCategory2 = categoryRepository.save(new Category("근육증진"));
        Category savedCategory3 = categoryRepository.save(new Category("영양식단"));

        //when
        List<CategoryResponse> response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("category/get-categories-success"))
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

        assertThat(response).containsExactlyInAnyOrder(expected, expected2, expected3);
    }

    @DisplayName("등록된 카테고리가 없을 때, 200 OK 상태코드와 빈 카테고리 목록을 반환한다.")
    @Test
    void getAllCategories2() {
        //given
        //when
        List<CategoryResponse> response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("category/get-categories-empty"))
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
