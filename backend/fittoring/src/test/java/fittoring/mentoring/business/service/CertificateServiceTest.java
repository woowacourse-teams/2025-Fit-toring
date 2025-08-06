package fittoring.mentoring.business.service;

import fittoring.config.JpaConfiguration;
import fittoring.config.S3Configuration;
import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.CertificateType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.MemberRole;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.infra.S3Uploader;
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

    @Autowired
    private CertificateService certificateService;

    @Autowired
    TestEntityManager em;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("상태가 없는 자격증명 목록 조회는 모든 값을 반환한다.")
    @Test
    void getAllCertificates() {
        // given
        Member admin = new Member(
                "adminId",
                "남",
                "관리자",
                new Phone("010-9999-9999"),
                Password.from("admin123"),
                MemberRole.ADMIN
        );
        Member savedMember = em.persist(admin);
        Mentoring mentoring = new Mentoring(
                savedMember,
                1000,
                1,
                "content",
                "intro"
        );
        Mentoring savedMentoring = em.persist(mentoring);
        Certificate certificate1 = new Certificate(
                CertificateType.LICENSE,
                "자격증",
                savedMentoring
        );
        em.persist(certificate1);
        Certificate certificate2 = new Certificate(
                CertificateType.LICENSE,
                "자격증",
                savedMentoring
        );
        em.persist(certificate2);

        // when
        List<CertificateResponse> certificates = certificateService.getAllCertificates(savedMember.getId(), null);

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
        Member admin = new Member(
                "adminId",
                "남",
                "관리자",
                new Phone("010-9999-9999"),
                Password.from("admin123"),
                MemberRole.ADMIN
        );
        Member savedMember = em.persist(admin);
        Mentoring mentoring = new Mentoring(
                savedMember,
                1000,
                1,
                "content",
                "intro"
        );
        Mentoring savedMentoring = em.persist(mentoring);
        Certificate certificate1 = new Certificate(
                CertificateType.LICENSE,
                "자격증",
                savedMentoring
        );
        em.persist(certificate1);
        Certificate certificate2 = new Certificate(
                CertificateType.LICENSE,
                "자격증",
                savedMentoring
        );
        em.persist(certificate2);

        // when
        List<CertificateResponse> certificatesExpectedPending = certificateService.getAllCertificates(
                savedMember.getId(),
                Status.PENDING
        );
        List<CertificateResponse> certificatesExpectedApproved = certificateService.getAllCertificates(
                savedMember.getId(),
                Status.APPROVED
        );

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(certificatesExpectedPending).hasSize(2);
            softAssertions.assertThat(certificatesExpectedApproved).hasSize(0);
        });
    }
}