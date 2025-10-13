package fittoring.application.mentoring.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.CertificateNotFoundException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.image.service.ImageService;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.application.mentoring.presentation.dto.response.CertificateDetailResponse;
import fittoring.application.mentoring.presentation.dto.response.CertificateResponse;
import fittoring.application.mentoring.repository.MentoringPaginationHelper;
import fittoring.application.mentoring.service.dto.CertificateDeleteDto;
import fittoring.config.JpaConfiguration;
import fittoring.config.QueryDslConfig;
import fittoring.domain.model.Certificate;
import fittoring.domain.model.CertificateType;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Status;
import fittoring.domain.model.password.Password;
import fittoring.logging.JsonLogger;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({
        DbCleaner.class,
        CertificateService.class,
        ImageService.class,
        JpaConfiguration.class,
        QueryDslConfig.class,
        MentoringPaginationHelper.class
})
@DataJpaTest
class CertificateServiceTest {

    @MockitoBean
    private PresignedUrlService presignedUrlService;

    @MockitoBean
    private JsonLogger jsonLogger;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private DbCleaner dbCleaner;

    private Member admin;


    @BeforeEach
    void setUp() {
        dbCleaner.clean();
        admin = FixtureUtil.getTestAdmin();
        em.persist(admin);
        given(presignedUrlService.isObjectExistsFromKey(anyString()))
                .willReturn(true);
        given(presignedUrlService.isObjectExistsFromUrl(anyString()))
                .willReturn(true);
    }

    @DisplayName("관리자 권한이 없는 일반 사용자라면 자격증명 목록을 조회할 수 없다.")
    @Test
    void getAllWithoutAdminAuthority() {
        // given
        Member user = FixtureUtil.getTestMentee();
        em.persist(user);

        // when
        // then
        assertThatThrownBy(() -> certificateService.getAllCertificates(user.getId(), null))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("상태가 없는 자격증명 목록 조회는 모든 값을 반환한다.")
    @Test
    void getAllCertificates() {
        // given
        Member member = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(member));
        Certificate certificate1 = em.persist(FixtureUtil.getTestCertificate(mentoring));
        Certificate certificate2 = em.persist(FixtureUtil.getTestCertificate(mentoring));

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
        Member member = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(member)); // 부모 먼저
        Certificate certificate1 = em.persist(FixtureUtil.getTestCertificate(mentoring));
        Certificate certificate2 = em.persist(FixtureUtil.getTestCertificate(mentoring));

        // when
        List<CertificateResponse> pending = certificateService.getAllCertificates(admin.getId(), Status.PENDING);
        List<CertificateResponse> approved = certificateService.getAllCertificates(admin.getId(), Status.APPROVED);

        // then
        SoftAssertions.assertSoftly(s -> {
            s.assertThat(pending).hasSize(2);
            s.assertThat(approved).hasSize(0);
        });
    }

    @DisplayName("관리자 권한이 있다면 자격증명을 상세조회할 수 있다.")
    @Test
    void getOneForAdmin() {
        // given
        Member member = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(member));
        Certificate certificate = em.persist(FixtureUtil.getTestCertificate(mentoring));
        // certificate가 영속화된 뒤에는 id 존재
        Image image = em.persist(new Image("url", ImageType.CERTIFICATE, certificate.getId()));

        // when
        CertificateDetailResponse detail = certificateService.getCertificate(admin.getId(), mentoring.getId());

        // then
        SoftAssertions.assertSoftly(s -> {
            s.assertThat(detail.certificateName()).isEqualTo("자격증");
            s.assertThat(detail.certificateType()).isEqualTo(CertificateType.LICENSE);
        });
    }

    @DisplayName("관리자 권한이 없는 일반 사용자라면 자격증명을 상세조회 할 수 없다.")
    @Test
    void getOneWithoutAdminAuthority() {
        // given
        Member member = FixtureUtil.getTestMentee();
        em.persist(member);

        Mentoring mentoring = FixtureUtil.getTestMentoring(member);

        // when
        // then
        assertThatThrownBy(() -> certificateService.getCertificate(
                member.getId(),
                mentoring.getId()
        ))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("관리자 권한이 있으면 검토 중인 자격증명을 승인할 수 있다.")
    @Test
    void approveCertificateForAdmin() {
        Member member = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(member));
        Certificate certificate = em.persist(FixtureUtil.getTestCertificate(mentoring));

        assertThatCode(() -> certificateService.approveCertificate(admin.getId(), certificate.getId()))
                .doesNotThrowAnyException();
    }



    @DisplayName("관리자 권한이 없는 일반 사용자라면 검토 중인 자격증명을 승인할 수 없다.")
    @Test
    void approveCertificateWithoutAdminAuthority() {
        Member member = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(member));
        Certificate certificate = em.persist(FixtureUtil.getTestCertificate(mentoring));

        assertThatThrownBy(() -> certificateService.approveCertificate(member.getId(), certificate.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("관리자 권한이 있으면 검토 중인 자격증명을 거절할 수 있다.")
    @Test
    void rejectCertificateForAdmin() {
        Member member = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(member));
        Certificate certificate = em.persist(FixtureUtil.getTestCertificate(mentoring));

        assertThatCode(() -> certificateService.rejectCertificate(admin.getId(), certificate.getId()))
                .doesNotThrowAnyException();
    }

    @DisplayName("관리자 권한이 없는 일반 사용자라면 검토 중인 자격증명을 거절할 수 없다.")
    @Test
    void rejectCertificateWithoutAdminAuthority() {
        Member member = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(member));
        Certificate certificate = em.persist(FixtureUtil.getTestCertificate(mentoring));

        assertThatThrownBy(() -> certificateService.rejectCertificate(member.getId(), certificate.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("존재하지 않는 자격 사항 삭제 요청 시 예외가 발생한다.")
    @Test
    void deleteCertificateFail1() {
        // given
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        CertificateDeleteDto dto = new CertificateDeleteDto(mentee.getId(), 999L);

        // when
        // then
        assertThatThrownBy(() -> certificateService.deleteCertificate(dto))
                .isInstanceOf(CertificateNotFoundException.class)
                .hasMessage(BusinessErrorMessage.CERTIFICATE_NOT_FOUND.getMessage());
    }

    @DisplayName("본인의 것이 아닌 자격 사항을 삭제하려고 하면 예외가 발생한다")
    @Test
    void deleteReviewFail2() {
        // given
        Member mentorKim = em.persist(new Member(
                "mentorId",
                "MALE",
                "김트레이너",
                new Phone("010-1111-2222"),
                Password.from("password")
        ));
        Member mentorPark = em.persist(new Member(
                "mentorId2",
                "MALE",
                "박트레이너",
                new Phone("010-1111-2223"),
                Password.from("password")
        ));
        Mentoring parkMentoring = em.persist(new Mentoring(
                mentorPark,
                5000,
                10,
                "박트레이너의 멘토링",
                "박트레이너는 컴퓨터에 빠삭합니다.",
                "가상의오픈채팅링크"
        ));
        Certificate parkLicense = em.persist(new Certificate(CertificateType.LICENSE, "정보처리기사", parkMentoring));

        CertificateDeleteDto dto = new CertificateDeleteDto(mentorKim.getId(), parkLicense.getId());

        // when
        // then
        assertThatThrownBy(() -> certificateService.deleteCertificate(dto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.NOT_CERTIFICATE_OWNER.getMessage());
    }
}
