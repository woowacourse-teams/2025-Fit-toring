package fittoring.admin.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import fittoring.admin.presentation.dto.AdminCertificateResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.image.service.ImageService;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.application.mentoring.presentation.dto.response.CertificateDetailResponse;
import fittoring.application.mentoring.repository.MentoringPaginationHelper;
import fittoring.config.JpaConfiguration;
import fittoring.config.QueryDslConfig;
import fittoring.domain.model.Certificate;
import fittoring.domain.model.CertificateType;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Status;
import fittoring.infrastructure.image.KeyBuilder;
import fittoring.logging.JsonLogger;
import fittoring.util.DbCleaner;
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
    AdminCertificateService.class,
    ImageService.class,
    JpaConfiguration.class,
    QueryDslConfig.class,
    MentoringPaginationHelper.class,
    KeyBuilder.class
})
@DataJpaTest
class AdminCertificateServiceTest {

    @MockitoBean
    private PresignedUrlService presignedUrlService;

    @MockitoBean
    private JsonLogger jsonLogger;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private AdminCertificateService adminCertificateService;

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
        assertThatThrownBy(() -> adminCertificateService.getAllCertificatesPaged(user.getId(), null, 1, 20))
            .isInstanceOf(ForbiddenException.class)
            .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("상태가 없는 자격증명을 페이지네이션하여 반환한다.")
    @Test
    void getAllCertificatesPaged() {
        // given
        Member member = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(member));
        for (int i=0; i<35; i++) {
            // APPROVED 자격증명 35개
            Certificate certificate = FixtureUtil.getTestCertificate(mentoring);
            certificate.approve();
            em.persist(certificate);
        }
        for (int i=0; i<35; i++) {
            // REJECTED 자격증명 35개
            Certificate certificate = FixtureUtil.getTestCertificate(mentoring);
            certificate.reject();
            em.persist(certificate);
        }
        for (int i=0; i<35; i++) {
            // PENDING 자격증명 35개
            Certificate certificate = FixtureUtil.getTestCertificate(mentoring);
            em.persist(certificate);
        }
        em.flush();
        em.clear();

        // when
        PageResult<AdminCertificateResponse> firstResponse = adminCertificateService.getAllCertificatesPaged(admin.getId(), null, 1, 20);
        PageResult<AdminCertificateResponse> secondResponse = adminCertificateService.getAllCertificatesPaged(admin.getId(), null, 2, 20);
        PageResult<AdminCertificateResponse> thirdResponse = adminCertificateService.getAllCertificatesPaged(admin.getId(), null, 3, 20);
        PageResult<AdminCertificateResponse> fourthResponse = adminCertificateService.getAllCertificatesPaged(admin.getId(), null, 4, 20);
        PageResult<AdminCertificateResponse> fifthResponse = adminCertificateService.getAllCertificatesPaged(admin.getId(), null, 5, 20);
        PageResult<AdminCertificateResponse> sixthResponse = adminCertificateService.getAllCertificatesPaged(admin.getId(), null, 6, 20);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(firstResponse.content()).hasSize(20);
            softAssertions.assertThat(firstResponse.size()).isEqualTo(20);
            softAssertions.assertThat(firstResponse.total()).isEqualTo(105);
            softAssertions.assertThat(firstResponse.totalPages()).isEqualTo(6);
            softAssertions.assertThat(firstResponse.page()).isEqualByComparingTo(1);
            softAssertions.assertThat(secondResponse.content()).hasSize(20);
            softAssertions.assertThat(secondResponse.size()).isEqualTo(20);
            softAssertions.assertThat(secondResponse.total()).isEqualTo(105);
            softAssertions.assertThat(secondResponse.totalPages()).isEqualTo(6);
            softAssertions.assertThat(secondResponse.page()).isEqualByComparingTo(2);
            softAssertions.assertThat(thirdResponse.content()).hasSize(20);
            softAssertions.assertThat(thirdResponse.size()).isEqualTo(20);
            softAssertions.assertThat(thirdResponse.total()).isEqualTo(105);
            softAssertions.assertThat(thirdResponse.totalPages()).isEqualTo(6);
            softAssertions.assertThat(thirdResponse.page()).isEqualByComparingTo(3);
            softAssertions.assertThat(fourthResponse.content()).hasSize(20);
            softAssertions.assertThat(fourthResponse.size()).isEqualTo(20);
            softAssertions.assertThat(fourthResponse.total()).isEqualTo(105);
            softAssertions.assertThat(fourthResponse.totalPages()).isEqualTo(6);
            softAssertions.assertThat(fourthResponse.page()).isEqualByComparingTo(4);
            softAssertions.assertThat(fifthResponse.content()).hasSize(20);
            softAssertions.assertThat(fifthResponse.size()).isEqualTo(20);
            softAssertions.assertThat(fifthResponse.total()).isEqualTo(105);
            softAssertions.assertThat(fifthResponse.totalPages()).isEqualTo(6);
            softAssertions.assertThat(fifthResponse.page()).isEqualByComparingTo(5);
            softAssertions.assertThat(sixthResponse.content()).hasSize(5);
            softAssertions.assertThat(sixthResponse.size()).isEqualTo(5);
            softAssertions.assertThat(sixthResponse.total()).isEqualTo(105);
            softAssertions.assertThat(sixthResponse.totalPages()).isEqualTo(6);
            softAssertions.assertThat(sixthResponse.page()).isEqualByComparingTo(6);
        });
    }

    @DisplayName("상태 필터링을 거친 자격증명을 페이지네이션하여 반환한다.")
    @Test
    void getAllCertificatesPaged2() {
        // given
        Member member = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(member));
        for (int i=0; i<35; i++) {
            // APPROVED 자격증명 35개
            Certificate certificate = FixtureUtil.getTestCertificate(mentoring);
            certificate.approve();
            em.persist(certificate);
        }
        for (int i=0; i<35; i++) {
            // REJECTED 자격증명 35개
            Certificate certificate = FixtureUtil.getTestCertificate(mentoring);
            certificate.reject();
            em.persist(certificate);
        }
        for (int i=0; i<35; i++) {
            // PENDING 자격증명 35개
            Certificate certificate = FixtureUtil.getTestCertificate(mentoring);
            em.persist(certificate);
        }
        em.flush();
        em.clear();

        // when
        PageResult<AdminCertificateResponse> firstResponse = adminCertificateService.getAllCertificatesPaged(admin.getId(), Status.REJECTED, 1, 20);
        PageResult<AdminCertificateResponse> secondResponse = adminCertificateService.getAllCertificatesPaged(admin.getId(), Status.REJECTED, 2, 20);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(firstResponse.content()).hasSize(20);
            softAssertions.assertThat(firstResponse.size()).isEqualTo(20);
            softAssertions.assertThat(firstResponse.total()).isEqualTo(35);
            softAssertions.assertThat(firstResponse.totalPages()).isEqualTo(2);
            softAssertions.assertThat(firstResponse.page()).isEqualByComparingTo(1);
            softAssertions.assertThat(secondResponse.content()).hasSize(15);
            softAssertions.assertThat(secondResponse.size()).isEqualTo(15);
            softAssertions.assertThat(secondResponse.total()).isEqualTo(35);
            softAssertions.assertThat(secondResponse.totalPages()).isEqualTo(2);
            softAssertions.assertThat(secondResponse.page()).isEqualByComparingTo(2);
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
        Image image = em.persist(new Image("url", ImageType.CERTIFICATE, certificate.getId(), "baseName"));

        // when
        CertificateDetailResponse detail = adminCertificateService.getCertificate(admin.getId(), mentoring.getId());

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
        assertThatThrownBy(() -> adminCertificateService.getCertificate(
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

        assertThatCode(() -> adminCertificateService.approveCertificate(admin.getId(), certificate.getId()))
            .doesNotThrowAnyException();
    }



    @DisplayName("관리자 권한이 없는 일반 사용자라면 검토 중인 자격증명을 승인할 수 없다.")
    @Test
    void approveCertificateWithoutAdminAuthority() {
        Member member = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(member));
        Certificate certificate = em.persist(FixtureUtil.getTestCertificate(mentoring));

        assertThatThrownBy(() -> adminCertificateService.approveCertificate(member.getId(), certificate.getId()))
            .isInstanceOf(ForbiddenException.class)
            .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("관리자 권한이 있으면 검토 중인 자격증명을 거절할 수 있다.")
    @Test
    void rejectCertificateForAdmin() {
        Member member = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(member));
        Certificate certificate = em.persist(FixtureUtil.getTestCertificate(mentoring));

        assertThatCode(() -> adminCertificateService.rejectCertificate(admin.getId(), certificate.getId()))
            .doesNotThrowAnyException();
    }

    @DisplayName("관리자 권한이 없는 일반 사용자라면 검토 중인 자격증명을 거절할 수 없다.")
    @Test
    void rejectCertificateWithoutAdminAuthority() {
        Member member = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(member));
        Certificate certificate = em.persist(FixtureUtil.getTestCertificate(mentoring));

        assertThatThrownBy(() -> adminCertificateService.rejectCertificate(member.getId(), certificate.getId()))
            .isInstanceOf(ForbiddenException.class)
            .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }
}
