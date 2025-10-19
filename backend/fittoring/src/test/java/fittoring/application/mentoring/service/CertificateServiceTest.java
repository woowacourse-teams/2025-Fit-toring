package fittoring.application.mentoring.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.CertificateNotFoundException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.image.service.ImageService;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.application.mentoring.repository.MentoringPaginationHelper;
import fittoring.application.mentoring.service.dto.CertificateDeleteDto;
import fittoring.config.JpaConfiguration;
import fittoring.config.QueryDslConfig;
import fittoring.domain.model.Certificate;
import fittoring.domain.model.CertificateType;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.password.Password;
import fittoring.infrastructure.image.KeyBuilder;
import fittoring.logging.JsonLogger;
import fittoring.util.DbCleaner;
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
        KeyBuilder.class,
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
