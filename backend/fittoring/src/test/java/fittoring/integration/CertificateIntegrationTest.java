package fittoring.integration;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.CertificateRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.domain.model.Certificate;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CertificateIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("자격증 삭제에 성공하면 204 No Content를 반환한다")
    @Test
    void deleteCertificate() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));
        Certificate certificate = certificateRepository.save(FixtureUtil.getTestCertificate(mentoring));

        String accessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());

        // when
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("certificate/delete-certificate-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .delete("/certificates/{certificateId}", certificate.getId())
                .then().log().all()
                .statusCode(204);

        // then
        assertThat(certificateRepository.findById(certificate.getId())).isEmpty();
    }

    @DisplayName("본인의 자격증이 아닌 경우 삭제에 실패하고 403 Forbidden을 반환한다")
    @Test
    void deleteCertificateFail_NotOwner() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));
        Certificate certificate = certificateRepository.save(FixtureUtil.getTestCertificate(mentoring));

        Member otherMember = memberRepository.save(FixtureUtil.getTestMentee());
        String accessToken = jwtProvider.createAccessToken(otherMember.getId(), otherMember.getRole());

        // when
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("certificate/delete-certificate-fail-not-owner"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .delete("/certificates/{certificateId}", certificate.getId())
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("존재하지 않는 자격증을 삭제하려고 하면 404 Not Found를 반환한다")
    @Test
    void deleteCertificateFail_NotFound() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        String accessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());
        long invalidCertificateId = 999L;

        // when
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("certificate/delete-certificate-fail-not-found"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .delete("/certificates/{certificateId}", invalidCertificateId)
                .then().log().all()
                .statusCode(404);
    }
}
