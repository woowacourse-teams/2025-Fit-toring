package fittoring;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import com.epages.restdocs.apispec.ResourceSnippet;
import com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.infrastructure.OauthClientService;
import fittoring.infrastructure.SmsRestClientService;
import fittoring.util.DbCleaner;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentationConfigurer;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractApiDocumentationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected DbCleaner dbCleaner;

    @MockitoBean
    protected PresignedUrlService presignedUrlService;

    @MockitoBean
    protected SmsRestClientService smsRestClientService;

    @MockitoBean
    protected OauthClientService oauthClientService;

    protected RequestSpecification spec;

    protected RestDocumentationFilter documentWithTag(String id) {
        String tag = id.contains("/") ? id.substring(0, id.indexOf('/')) : id;
        return RestAssuredRestDocumentationWrapper.document(
                id,
                resource(builder().tag(tag).build())
        );
    }

    protected RestDocumentationFilter documentWithTag(String id, ResourceSnippet resourceSnippet) {
        return RestAssuredRestDocumentationWrapper.document(
                id,
                resourceSnippet
        );
    }

    @BeforeEach
    protected void setUp(RestDocumentationContextProvider restDocumentation) {
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
}
