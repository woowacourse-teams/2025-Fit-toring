package fittoring.integration.admin;

import fittoring.AbstractApiDocumentationTest;
import fittoring.admin.presentation.dto.AdminDeviceResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.repository.DeviceRepository;
import fittoring.domain.model.Member;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;

public class AdminDeviceIntegrationTest extends AbstractApiDocumentationTest {

    private Member admin;
    private String adminAccessToken;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private DeviceRepository deviceRepository;

    @BeforeEach
    protected void setUp(RestDocumentationContextProvider restDocumentation) {
        super.setUp(restDocumentation);
        admin = memberRepository.save(FixtureUtil.testAdmin());
        adminAccessToken = jwtProvider.createAccessToken(admin.getId(), admin.getRole());
    }

    @DisplayName("관리자는 디바이스를 삭제할 수 있다.")
    @Test
    void getAllDevicesWithAuthority() {
        // given
        for (int i = 0; i < 5; i++) {
            Member mentee = memberRepository.save(FixtureUtil.testMentee(i));
            for (int j = 0; j < 5; j++) {
                deviceRepository.save(FixtureUtil.testDevices(mentee, Integer.toString(j)));
            }
        }
        Assertions.assertThat(deviceRepository.count()).isEqualTo(25);

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("admin/get-admin-devices-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", adminAccessToken)
                .when()
                .delete("/admin/devices/{deviceId}", 1)
                .then().log().all()
                .statusCode(204);
        Assertions.assertThat(deviceRepository.count()).isEqualTo(24);
    }

    @DisplayName("관리자 디바이스 목록 조회")
    @Nested
    class AllDevices {

        @DisplayName("관리자는 디바이스 목록 조회에 성공한다.")
        @Test
        void getAllDevicesWithAuthority() {
            // given
            for (int i = 0; i < 5; i++) {
                Member mentee = memberRepository.save(FixtureUtil.testMentee(i));
                for (int j = 0; j < 5; j++) {
                    deviceRepository.save(FixtureUtil.testDevices(mentee, Integer.toString(j)));
                }
            }

            // when
            // then
            PageResult<AdminDeviceResponse> actual = RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("admin/get-admin-devices-success"))
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .when()
                    .get("/admin/devices")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<>() {
                    });

            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(actual.total()).isEqualTo(25);
                softAssertions.assertThat(actual.content()).hasSize(20);
                softAssertions.assertThat(actual.totalPages()).isEqualTo(2);
            });
        }

        @DisplayName("일반 사용자는 디바이스 목록 조회에 실패한다.")
        @Test
        void getAllDevicesWithoutAdminAuthority() {
            // given
            Member mentee = memberRepository.save(FixtureUtil.testMentee());
            String userAccessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());
            // when
            // then
            RestAssured
                    .given(spec)
                    .accept("application/json")
                    .filter(documentWithTag("admin/get-admin-devices-unauthorized"))
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", userAccessToken)
                    .when()
                    .get("/admin/devices")
                    .then().log().all()
                    .statusCode(403);
        }
    }
}
