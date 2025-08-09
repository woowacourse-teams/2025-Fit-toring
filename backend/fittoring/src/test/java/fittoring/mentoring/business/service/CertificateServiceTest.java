package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.config.JpaConfiguration;
import fittoring.config.S3Configuration;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.ForbiddenMemberException;
import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.CertificateType;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.MemberRole;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.infra.S3Uploader;
import fittoring.mentoring.presentation.dto.CertificateDetailResponse;
import fittoring.mentoring.presentation.dto.CertificateResponse;
import fittoring.util.DbCleaner;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({
        DbCleaner.class,
        CertificateService.class,
        ImageService.class,
        S3Uploader.class,
        S3Configuration.class,
        JpaConfiguration.class
})
@DataJpaTest
class CertificateServiceTest {

    private Member admin;
    private Mentoring mentoring;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    TestEntityManager em;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
        admin = new Member(
                "adminId",
                "여",
                "관리자",
                new Phone("010-9999-9999"),
                Password.from("admin123"),
                MemberRole.ADMIN
        );
        em.persist(admin);
        mentoring = new Mentoring(
                admin,
                1000,
                1,
                "content",
                "intro"
        );
        em.persist(mentoring);
    }

    @DisplayName("관리자 권한이 없는 일반 사용자라면 자격증명 목록을 조회할 수 없다.")
    @Test
    void getAllWithoutAdminAuthority() {
        // given
        Member user = new Member(
                "userId",
                "여",
                "유저",
                new Phone("010-1111-2222"),
                Password.from("1234")
        );
        em.persist(user);

        // when
        // then
        assertThatThrownBy(() -> certificateService.getAllCertificates(user.getId(), null))
                .isInstanceOf(ForbiddenMemberException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("상태가 없는 자격증명 목록 조회는 모든 값을 반환한다.")
    @Test
    void getAllCertificates() {
        // given
        Certificate certificate1 = new Certificate(
                CertificateType.LICENSE,
                "자격증",
                mentoring
        );
        em.persist(certificate1);
        Certificate certificate2 = new Certificate(
                CertificateType.LICENSE,
                "자격증",
                mentoring
        );
        em.persist(certificate2);

        // when
        List<CertificateResponse> certificates = certificateService.getAllCertificates(admin.getId(), null);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(certificates).hasSize(2);
            softAssertions.assertThat(certificates.get(0).id()).isEqualTo(certificate1.getId());
            softAssertions.assertThat(certificates.get(1).id()).isEqualTo(certificate2.getId());
        });
    }

    @DisplayName("상태가 있는 자격증명 목록을 필터링해서 반환한다.")
    @Test
    void getAllCertificationWithStatus() {
        // given
        Certificate certificate1 = new Certificate(
                CertificateType.LICENSE,
                "자격증",
                mentoring
        );
        em.persist(certificate1);
        Certificate certificate2 = new Certificate(
                CertificateType.LICENSE,
                "자격증",
                mentoring
        );
        em.persist(certificate2);

        // when
        List<CertificateResponse> certificatesExpectedPending = certificateService.getAllCertificates(
                admin.getId(),
                Status.PENDING
        );
        List<CertificateResponse> certificatesExpectedApproved = certificateService.getAllCertificates(
                admin.getId(),
                Status.APPROVED
        );

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(certificatesExpectedPending).hasSize(2);
            softAssertions.assertThat(certificatesExpectedApproved).hasSize(0);
        });
    }

    @DisplayName("관리자 권한이 있다면 자격증명을 상세조회할 수 있다.")
    @Test
    void getOneForAdmin() {
        // given
        CertificateType type = CertificateType.LICENSE;
        String name = "자격증";
        Certificate certificate = new Certificate(
                type,
                name,
                mentoring
        );
        em.persist(certificate);
        Image image = new Image(
                "url",
                ImageType.CERTIFICATE,
                certificate.getId()
        );
        em.persist(image);

        // when
        CertificateDetailResponse certificateDetailResponse = certificateService.getCertificate(
                admin.getId(),
                mentoring.getId()
        );

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(certificateDetailResponse.certificateName()).isEqualTo(name);
            softAssertions.assertThat(certificateDetailResponse.certificateType()).isEqualTo(CertificateType.LICENSE);
        });
    }

    @DisplayName("관리자 권한이 없는 일반 사용자라면 자격증명을 상세조회 할 수 없다.")
    @Test
    void getOneWithoutAdminAuthority() {
        // given
        Member user = new Member(
                "userId",
                "여",
                "유저",
                new Phone("010-1111-2222"),
                Password.from("1234")
        );
        em.persist(user);

        // when
        // then
        assertThatThrownBy(() -> certificateService.getCertificate(
                user.getId(),
                mentoring.getId()
        ))
                .isInstanceOf(ForbiddenMemberException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("관리자 권한이 있으면 검토 중인 자격증명을 승인할 수 있다.")
    @Test
    void approveCertificateForAdmin() {
        // given
        CertificateType type = CertificateType.LICENSE;
        String name = "자격증";
        Certificate certificate = new Certificate(
                type,
                name,
                mentoring
        );
        em.persist(certificate);

        // when
        // then
        assertThatCode(() -> certificateService.approveCertificate(admin.getId(), certificate.getId()))
                .doesNotThrowAnyException();
    }

    @DisplayName("관리자 권한이 없는 일반 사용자라면 검토 중인 자격증명을 승인할 수 없다.")
    @Test
    void approveCertificateWithoutAdminAuthority() {
        // given
        Member user = new Member(
                "userId",
                "여",
                "유저",
                new Phone("010-1111-2222"),
                Password.from("1234")
        );
        em.persist(user);
        CertificateType type = CertificateType.LICENSE;
        String name = "자격증";
        Certificate certificate = new Certificate(
                type,
                name,
                mentoring
        );
        em.persist(certificate);

        // when
        // then
        assertThatThrownBy(() -> certificateService.approveCertificate(user.getId(), certificate.getId()))
                .isInstanceOf(ForbiddenMemberException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("관리자 권한이 있으면 검토 중인 자격증명을 거절할 수 있다.")
    @Test
    void rejectCertificateForAdmin() {
        // given
        CertificateType type = CertificateType.LICENSE;
        String name = "자격증";
        Certificate certificate = new Certificate(
                type,
                name,
                mentoring
        );
        em.persist(certificate);

        // when
        // then
        assertThatCode(() -> certificateService.rejectCertificate(admin.getId(), certificate.getId()))
                .doesNotThrowAnyException();
    }

    @DisplayName("관리자 권한이 없는 일반 사용자라면 검토 중인 자격증명을 거절할 수 없다.")
    @Test
    void rejectCertificateWithoutAdminAuthority() {
        // given
        Member user = new Member(
                "userId",
                "여",
                "유저",
                new Phone("010-1111-2222"),
                Password.from("1234")
        );
        em.persist(user);
        CertificateType type = CertificateType.LICENSE;
        String name = "자격증";
        Certificate certificate = new Certificate(
                type,
                name,
                mentoring
        );
        em.persist(certificate);

        // when
        // then
        assertThatThrownBy(() -> certificateService.rejectCertificate(user.getId(), certificate.getId()))
                .isInstanceOf(ForbiddenMemberException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }
}
