package fittoring.integration.mentoring.api.admin;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.snippet.Attributes.key;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import fittoring.integration.mentoring.api.AbstractApiDocumentationTest;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Phone;
import fittoring.domain.model.password.Password;
import fittoring.application.repository.MemberRepository;
import fittoring.application.service.ImageService;
import fittoring.application.service.JwtProvider;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.MultiPartSpecification;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;

@Import(AdminImageControllerTest.TestMockConfig.class)
class AdminImageControllerTest extends AbstractApiDocumentationTest {

    private Member admin;
    private Member user;
    private String adminAccessToken;
    private String userAccessToken;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private ImageService imageService;

    @TestConfiguration
    static class TestMockConfig {
        @Bean
        ImageService imageService() {
            return Mockito.mock(ImageService.class);
        }
    }

    @BeforeEach
    protected void setUp(RestDocumentationContextProvider restDocumentation) {
        super.setUp(restDocumentation);

        admin = memberRepository.save(new Member(
                "adminId", "남", "관리자", new Phone("010-0000-0000"),
                Password.from("pw"), MemberRole.ADMIN
        ));
        adminAccessToken = jwtProvider.createAccessToken(admin.getId());

        user = memberRepository.save(new Member(
                "userId", "남", "멘티", new Phone("010-1111-1111"),
                Password.from("pw")
        ));
        userAccessToken = jwtProvider.createAccessToken(user.getId());
    }

    @DisplayName("관리자가 아닌 사용자가 이미지 업로드를 요청하면 403을 반환한다.")
    @Test
    void returnForbiddenWhenNonAdmin() throws IOException {
        // given
        var file = new MockMultipartFile("image", "file.jpg", "image/jpeg", "bytes".getBytes());
        String dataJson = """
                    { "imageType": "MENTORING_PROFILE", "relationId": 123 }
                """;
        MultiPartSpecification jsonPart = new MultiPartSpecBuilder(dataJson)
                .controlName("data")
                .mimeType("application/json")
                .fileName("payload.json")
                .charset(StandardCharsets.UTF_8)
                .build();

        // when
        // then
        RestAssured.given(spec)
                .log().all()
                .accept("application/json")
                .contentType(ContentType.MULTIPART)
                .cookie("accessToken", userAccessToken)
                .multiPart("image", file.getOriginalFilename(), file.getBytes(), file.getContentType())
                .multiPart(jsonPart)
                .filter(document(
                        "admin/post-images-forbidden",
                        requestParts(
                                partWithName("image")
                                        .description("업로드 이미지 파일")
                                        .attributes(
                                                key("type").value("file"),
                                                key("format").value("binary"),           // Swagger에 binary로 표기
                                                key("example").value("file.jpg")
                                        ),
                                partWithName("data")
                                        .description("이미지 메타데이터(JSON)")
                                        .attributes(
                                                key("contentType").value("application/json"),
                                                key("example").value("""
                                                            {"imageType":"MENTORING_PROFILE","relationId":123}
                                                        """)
                                        )
                        ),
                        requestPartFields("data",
                                fieldWithPath("imageType").type(JsonFieldType.STRING)
                                        .description("이미지 타입 (예: MENTORING_PROFILE)")
                                        .attributes(key("example").value("MENTORING_PROFILE")),
                                fieldWithPath("relationId").type(JsonFieldType.NUMBER)
                                        .description("연관 엔티티 ID")
                                        .attributes(key("example").value(123))
                        ),
                        responseFields(
                                fieldWithPath("status").description("에러 상태"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("timestamp").description("발생 시각")
                        ),
                        resource(ResourceSnippetParameters.builder()
                                .tag("admin")
                                .summary("이미지 업로드(권한 없음)")
                                .description("관리자가 아닌 사용자의 이미지 업로드 요청은 403을 반환합니다.")
                                .requestSchema(Schema.schema("AdminImageUploadMultipart"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())
                ))
                .when()
                .post("/admin/images")
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("관리자가 이미지 업로드를 요청하면 201과 저장된 이미지 리스트를 반환한다.")
    @Test
    void returnCreatedAndImages() throws IOException {
        // given
        var file = new MockMultipartFile("image", "file.jpg", "image/jpeg", "bytes".getBytes());
        var imageType = ImageType.MENTORING_PROFILE;
        long relationId = 777L;

        String dataJson = """
                    { "imageType": "MENTORING_PROFILE", "relationId": 777 }
                """;
        MultiPartSpecification jsonPart = new MultiPartSpecBuilder(dataJson)
                .controlName("data")
                .mimeType("application/json")
                .fileName("payload.json")
                .charset(StandardCharsets.UTF_8)
                .build();

        var savedDefault = new Image(
                "https://s3.../profile-image/default/uuid.jpg",
                ImageType.MENTORING_PROFILE, ImageVariant.DEFAULT, relationId
        );
        var savedThumb = new Image(
                "https://s3.../profile-image/thumbnail/uuid.jpg",
                ImageType.MENTORING_PROFILE, ImageVariant.THUMBNAIL, relationId
        );

        Mockito.when(imageService.uploadImageToS3(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(ImageType.getDir(imageType)),
                ArgumentMatchers.eq(imageType),
                ArgumentMatchers.eq(relationId)
        )).thenReturn(List.of(savedDefault, savedThumb));

        // when
        // then
        RestAssured.given(spec)
                .log().all()
                .accept("application/json")
                .contentType(ContentType.MULTIPART)
                .cookie("accessToken", adminAccessToken)
                .multiPart("image", file.getOriginalFilename(), file.getBytes(), file.getContentType())
                .multiPart(jsonPart)
                .filter(document(
                        "admin/post-images-success",
                        requestParts(
                                partWithName("image")
                                        .description("업로드 이미지 파일")
                                        .attributes(
                                                key("type").value("file"),
                                                key("format").value("binary"),
                                                key("example").value("file.jpg")
                                        ),
                                partWithName("data")
                                        .description("이미지 메타데이터(JSON)")
                                        .attributes(
                                                key("contentType").value("application/json"),
                                                key("example").value("""
                                                            {"imageType":"MENTORING_PROFILE","relationId":777}
                                                        """)
                                        )
                        ),
                        requestPartFields("data",
                                fieldWithPath("imageType").type(JsonFieldType.STRING)
                                        .description("이미지 타입")
                                        .attributes(key("example").value("MENTORING_PROFILE")),
                                fieldWithPath("relationId").type(JsonFieldType.NUMBER)
                                        .description("연관 ID")
                                        .attributes(key("example").value(777))
                        ),
                        responseFields(
                                fieldWithPath("[].url").type(JsonFieldType.STRING).description("이미지 URL")
                                        .attributes(
                                                key("example").value("https://s3.../profile-image/default/uuid.jpg")),
                                fieldWithPath("[].imageType").type(JsonFieldType.STRING).description("이미지 타입")
                                        .attributes(key("example").value("MENTORING_PROFILE")),
                                fieldWithPath("[].imageVariant").type(JsonFieldType.STRING).description("이미지 변형")
                                        .attributes(key("example").value("DEFAULT")),
                                fieldWithPath("[].relationId").type(JsonFieldType.NUMBER).description("연관 ID")
                                        .attributes(key("example").value(777))
                        ),
                        resource(ResourceSnippetParameters.builder()
                                .tag("admin")
                                .summary("이미지 업로드")
                                .description("관리자가 이미지 파일과 메타데이터(JSON)를 멀티파트로 업로드합니다.")
                                .requestSchema(Schema.schema("AdminImageUploadMultipart"))
                                .responseSchema(Schema.schema("ImageResponseList"))
                                .build())
                ))
                .when()
                .post("/admin/images")
                .then().log().all()
                .statusCode(201);
    }
}
