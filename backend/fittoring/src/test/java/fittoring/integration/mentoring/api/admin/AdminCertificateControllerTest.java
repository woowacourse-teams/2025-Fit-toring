package fittoring.integration.mentoring.api.admin;

import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.CertificateType;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.MemberRole;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.CertificateRepository;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.service.JwtProvider;
import fittoring.util.DbCleaner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminCertificateControllerTest {

    private Member admin;
    private Member user;
    private String adminAccessToken;
    private String userAccessToken;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @LocalServerPort
    public int port;

    @Autowired
    private DbCleaner dbCleaner;
    @Autowired
    private MentoringRepository mentoringRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        dbCleaner.clean();
        admin = memberRepository.save(new Member(
                "adminId",
                "남",
                "관리자",
                new Phone("010-0000-0000"),
                Password.from("pw"),
                MemberRole.ADMIN
        ));
        adminAccessToken = jwtProvider.createAccessToken(admin.getId());
        user = memberRepository.save(new Member(
                "userId",
                "남",
                "멘티",
                new Phone("010-1111-1111"),
                Password.from("pw")
        ));
        userAccessToken = jwtProvider.createAccessToken(user.getId());
    }

    @DisplayName("관리자 자격증명 목록 조회")
    @Nested
    class AllCertificates {

        @DisplayName("관리자는 자격증명 목록 조회에 성공한다.")
        @Test
        void getAllCertificatedWithAuthority() {
            // given
            // when
            // then
            RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .when()
                    .get("/admin/certificates")
                    .then().log().all()
                    .statusCode(200);
        }

        @DisplayName("일반 사용자는 자격증명 목록 조회에 실패한다.")
        @Test
        void getAllCertificatedWithoutAdminAuthority() {
            // given
            // when
            // then
            RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", userAccessToken)
                    .when()
                    .get("/admin/certificates")
                    .then().log().all()
                    .statusCode(403);
        }
    }

    @DisplayName("관리자 자격증명 상세 조회")
    @Nested
    class OneCertificate {
        @DisplayName("관리자는 자격 증명 상세 조회에 성공한다.")
        @Test
        void getCertificateWithAuthority() {
            // given
            Mentoring mentoring = mentoringRepository.save(new Mentoring(
                    admin,
                    1000,
                    1,
                    "content",
                    "introduction"
            ));
            Certificate certificate = certificateRepository.save(new Certificate(
                    CertificateType.LICENSE,
                    "자격증",
                    mentoring
            ));
            imageRepository.save(new Image(
                    "imageUrl",
                    ImageType.CERTIFICATE,
                    certificate.getId())
            );

            // when
            // then
            RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .when()
                    .get("/admin/certificates/" + certificate.getId())
                    .then().log().all()
                    .statusCode(200);
        }

        @DisplayName("일반 사용자는 자격증명 상세 조회에 실패한다.")
        @Test
        void getCertificateWithoutAdminAuthority() {
            // given
            Mentoring mentoring = mentoringRepository.save(new Mentoring(
                    admin,
                    1000,
                    1,
                    "content",
                    "introduction"
            ));
            Certificate certificate = certificateRepository.save(new Certificate(
                    CertificateType.LICENSE,
                    "자격증",
                    mentoring
            ));
            imageRepository.save(new Image(
                    "imageUrl",
                    ImageType.CERTIFICATE,
                    certificate.getId())
            );

            // when
            // then
            RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", userAccessToken)
                    .when()
                    .get("/admin/certificates/" + certificate.getId())
                    .then().log().all()
                    .statusCode(403);
        }
    }

    @DisplayName("관리자 자격증명 승인")
    @Nested
    class ApproveCertificate {

        @DisplayName("관리자는 자격 증명 승인에 성공한다.")
        @Test
        void approveCertificateWithAuthority() {
            // given
            Mentoring mentoring = mentoringRepository.save(new Mentoring(
                    admin,
                    1000,
                    1,
                    "content",
                    "introduction"
            ));
            Certificate certificate = certificateRepository.save(new Certificate(
                    CertificateType.LICENSE,
                    "자격증",
                    mentoring
            ));
            imageRepository.save(new Image(
                    "imageUrl",
                    ImageType.CERTIFICATE,
                    certificate.getId())
            );

            // when
            // then
            RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .when()
                    .post("/admin/certificates/" + certificate.getId() + "/approve")
                    .then().log().all()
                    .statusCode(204);
        }

        @DisplayName("일반 사용자는 자격증명 승인에 실패한다.")
        @Test
        void approveCertificateWithoutAdminAuthority() {
            // given
            Mentoring mentoring = mentoringRepository.save(new Mentoring(
                    admin,
                    1000,
                    1,
                    "content",
                    "introduction"
            ));
            Certificate certificate = certificateRepository.save(new Certificate(
                    CertificateType.LICENSE,
                    "자격증",
                    mentoring
            ));
            imageRepository.save(new Image(
                    "imageUrl",
                    ImageType.CERTIFICATE,
                    certificate.getId())
            );

            // when
            // then
            RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", userAccessToken)
                    .when()
                    .post("/admin/certificates/" + certificate.getId() + "/approve")
                    .then().log().all()
                    .statusCode(403);
        }
    }

    @DisplayName("관리자 자격증명 거절")
    @Nested
    class RejectCertificate {

        @DisplayName("관리자는 자격 증명 거절에 성공한다.")
        @Test
        void rejectCertificateWithAuthority() {
            // given
            Mentoring mentoring = mentoringRepository.save(new Mentoring(
                    admin,
                    1000,
                    1,
                    "content",
                    "introduction"
            ));
            Certificate certificate = certificateRepository.save(new Certificate(
                    CertificateType.LICENSE,
                    "자격증",
                    mentoring
            ));
            imageRepository.save(new Image(
                    "imageUrl",
                    ImageType.CERTIFICATE,
                    certificate.getId())
            );

            // when
            // then
            RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .when()
                    .post("/admin/certificates/" + certificate.getId() + "/reject")
                    .then().log().all()
                    .statusCode(204);
        }

        @DisplayName("일반 사용자는 자격증명 거절에 실패한다.")
        @Test
        void rejectCertificateWithoutAdminAuthority() {
            // given
            Mentoring mentoring = mentoringRepository.save(new Mentoring(
                    admin,
                    1000,
                    1,
                    "content",
                    "introduction"
            ));
            Certificate certificate = certificateRepository.save(new Certificate(
                    CertificateType.LICENSE,
                    "자격증",
                    mentoring
            ));
            imageRepository.save(new Image(
                    "imageUrl",
                    ImageType.CERTIFICATE,
                    certificate.getId())
            );

            // when
            // then
            RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", userAccessToken)
                    .when()
                    .post("/admin/certificates/" + certificate.getId() + "/reject")
                    .then().log().all()
                    .statusCode(403);
        }
    }
}