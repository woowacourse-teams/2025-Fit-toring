package fittoring.integration;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.CertificateRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.domain.model.Certificate;
import fittoring.domain.model.CertificateType;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.password.Password;
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
        Member mentor = memberRepository.save(new Member(
                "id1",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1234-9048"),
                Password.from("pw")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                3,
                "한 줄 소개",
                "긴 글 소개"
        ));
        Certificate certificate = certificateRepository.save(new Certificate(
                CertificateType.LICENSE,
                "운전면허증",
                mentoring
        ));

        String accessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());

        // when
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("certificate/delete-certificate-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .delete("/certificates/" + certificate.getId())
                .then().log().all()
                .statusCode(204);

        // then
        assertThat(certificateRepository.findById(certificate.getId())).isEmpty();
    }

    @DisplayName("본인의 자격증이 아닌 경우 삭제에 실패하고 403 Forbidden을 반환한다")
    @Test
    void deleteCertificateFail_NotOwner() {
        // given
        Member mentor = memberRepository.save(new Member(
                "id1",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1234-9048"),
                Password.from("pw")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                3,
                "한 줄 소개",
                "긴 글 소개"
        ));
        Certificate certificate = certificateRepository.save(new Certificate(
                CertificateType.LICENSE,
                "운전면허증",
                mentoring
        ));

        Member otherMember = memberRepository.save(new Member(
                "id2",
                Gender.FEMALE,
                "이회원",
                new Phone("010-5678-1234"),
                Password.from("pw")
        ));
        String accessToken = jwtProvider.createAccessToken(otherMember.getId(), otherMember.getRole());

        // when
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("certificate/delete-certificate-fail-not-owner"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .delete("/certificates/" + certificate.getId())
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("존재하지 않는 자격증을 삭제하려고 하면 404 Not Found를 반환한다")
    @Test
    void deleteCertificateFail_NotFound() {
        // given
        Member mentor = memberRepository.save(new Member(
                "id1",
                Gender.MALE,
                "김트레이너",
                new Phone("010-1234-9048"),
                Password.from("pw")
        ));
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
                .delete("/certificates/" + invalidCertificateId)
                .then().log().all()
                .statusCode(404);
    }
}
