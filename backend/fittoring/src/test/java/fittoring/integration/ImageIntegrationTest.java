package fittoring.integration;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import fittoring.AbstractApiDocumentationTest;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.image.presentation.dto.request.IssuedPresignedRequest;
import fittoring.application.image.presentation.dto.response.PresignedIssueResponse;
import fittoring.domain.model.ImageExtension;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.MemberRole;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class ImageIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("사용자가 이미지 업로드를 위한 Presigned URL을 발급하면 201 Create를 반환한다.")
    @Test
    void issuePresignedUrl() {
        // given
        IssuedPresignedRequest request = new IssuedPresignedRequest(
                ImageType.MENTORING_PROFILE,
                ImageExtension.JPG
        );

        PresignedIssueResponse response = new PresignedIssueResponse(
                "https://s3.ap-northeast-2.amazonaws.com/bucket/key?signature=...",
                LocalDateTime.now().plusMinutes(3)
        );

        String accessToken = jwtProvider.createAccessToken(1L, MemberRole.MENTEE);

        when(presignedUrlService.issuePresignedUrl(any())).thenReturn(response);

        // when // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("image/presigned-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("이미지")
                                .summary("Presigned URL 발급")
                                .description(
                                        "이미지 업로드를 위한 Presigned URL을 발급합니다. 성공 시 201 Created, 실패 시 500 Internal Server Error를 반환합니다.")
                                .requestSchema(Schema.schema("IssuedPresignedRequest"))
                                .requestFields(
                                        fieldWithPath("imageType").type(JsonFieldType.STRING)
                                                .description("이미지 타입 (MENTORING_PROFILE, etc.)"),
                                        fieldWithPath("extension").type(JsonFieldType.STRING)
                                                .description("이미지 확장자 (JPG, PNG, etc.)")
                                )
                                .responseSchema(Schema.schema("PresignedIssueResponse"))
                                .responseFields(
                                        fieldWithPath("presignedUrl").type(JsonFieldType.STRING)
                                                .description("발급된 Presigned URL"),
                                        fieldWithPath("expiresAt").type(JsonFieldType.STRING).description("URL 만료 시간")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/images/presigned")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("S3 Presigned URL 발급 중 예외가 발생하면 500 에러를 반환한다.")
    @Test
    void issuePresignedUrl_S3Exception() {
        // given
        IssuedPresignedRequest request = new IssuedPresignedRequest(
                ImageType.MENTORING_PROFILE,
                ImageExtension.JPG
        );

        String accessToken = jwtProvider.createAccessToken(1L, MemberRole.MENTEE);

        when(presignedUrlService.issuePresignedUrl(any()))
                .thenThrow(S3Exception.builder()
                        .message("S3 Error")
                        .statusCode(500)
                        .build());

        // when // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("image/presigned-error",
                        resource(ResourceSnippetParameters.builder()
                                .tag("이미지")
                                .requestSchema(Schema.schema("IssuedPresignedRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/images/presigned")
                .then().log().all()
                .statusCode(500);
    }
}
