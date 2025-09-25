package fittoring.integration.mentoring.api.admin;

import fittoring.integration.mentoring.api.AbstractApiDocumentationTest;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.ImageVariant;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.MemberRole;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.service.ImageService;
import fittoring.mentoring.business.service.JwtProvider;
import fittoring.mentoring.presentation.dto.ImageResponse;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.specification.MultiPartSpecification;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class AdminImageControllerTest extends AbstractApiDocumentationTest {

    private Member admin;
    private Member user;
    private String adminAccessToken;
    private String userAccessToken;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @MockitoBean
    private ImageService imageService;

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
    void returnForbiddenWhenNonAdmin() throws Exception {
        // given
        var file = new MockMultipartFile("image", "file.jpg", "image/jpeg", "bytes".getBytes());

        String dataJson = """
                  { "imageType": "MENTORING_PROFILE", "relationId": 123 }
                """;

        MultiPartSpecification jsonPart = new MultiPartSpecBuilder(dataJson)
                .controlName("data")
                .mimeType("application/json")
                .charset(StandardCharsets.UTF_8)
                .fileName("payload.json")
                .build();

        // when
        // then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.MULTIPART)
                .cookie("accessToken", userAccessToken)
                .multiPart("image", file.getOriginalFilename(), file.getBytes(), file.getContentType())
                .multiPart(jsonPart)
                .when()
                .post("/admin/images")
                .then()
                .log().all()
                .statusCode(403);
    }

    @DisplayName("관리자가 이미지 업로드를 요청하면 201과 저장된 이미지 리스트를 반환한다.")
    @Test
    void returnCreatedAndImages() throws Exception {
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
                .charset(StandardCharsets.UTF_8)
                .fileName("payload.json")
                .build();
        var savedDefault = new Image(
                "https://s3.../profile-image/default/uuid.jpg",
                ImageType.MENTORING_PROFILE,
                ImageVariant.DEFAULT,
                relationId
        );
        var savedThumb = new Image(
                "https://s3.../profile-image/thumbnail/uuid.jpg",
                ImageType.MENTORING_PROFILE,
                ImageVariant.THUMBNAIL,
                relationId
        );
        Mockito.when(imageService.uploadImageToS3(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(ImageType.getDir(imageType)),
                ArgumentMatchers.eq(imageType),
                ArgumentMatchers.eq(relationId)
        )).thenReturn(List.of(savedDefault, savedThumb));

        // when
        var actual = RestAssured.given()
                .log().all()
                .contentType(ContentType.MULTIPART)
                .cookie("accessToken", adminAccessToken)
                .multiPart("image", file.getOriginalFilename(), file.getBytes(), file.getContentType())
                .multiPart(jsonPart)
                .when()
                .post("/admin/images")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .as(new TypeRef<List<ImageResponse>>() {
                });

        // then
        var expected = List.of(
                new ImageResponse(savedDefault.getUrl(), savedDefault.getImageType(),
                        savedDefault.getImageVariant(), savedDefault.getRelationId()),
                new ImageResponse(savedThumb.getUrl(), savedThumb.getImageType(),
                        savedThumb.getImageVariant(), savedThumb.getRelationId())
        );
        Assertions.assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expected);
    }
}
