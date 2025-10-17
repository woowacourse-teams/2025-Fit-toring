package fittoring.integration.admin;

import fittoring.integration.AbstractApiDocumentationTest;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import fittoring.domain.model.Status;
import fittoring.domain.model.password.Password;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.application.review.repository.ReviewRepository;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.reservation.presentation.dto.request.ReservationStatusUpdateRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminReservationIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("관리자는 특정 멘토링에 달린 모든 예약을 조회 성공하면 200 OK를 반환한다")
    @Test
    void findMentoringReservations() {
        // given
        Member admin = memberRepository.save(new Member(
                "adminId",
                "MALE",
                "관리자",
                new Phone("010-1111-2222"),
                Password.from("password"),
                MemberRole.ADMIN
        ));
        Member mentor = memberRepository.save(new Member(
                "mentorId",
                "MALE",
                "아이유",
                new Phone("010-1234-5678"),
                Password.from("password"),
                MemberRole.MENTOR
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "Last Fantasy",
                "아직 모르는 게 많은 나 저 문을 열고 걸어 나가도 되겠죠 날 천천히 기다릴 수 있나요 기도해줘요 넘어지지 않도록",
                "가상의카카오오픈채팅"
        ));
        Member mentee1 = memberRepository.save(new Member(
                "menteeId1",
                "MALE",
                "김멘티",
                new Phone("010-1234-5679"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Member mentee2 = memberRepository.save(new Member(
                "menteeId2",
                "MALE",
                "박멘티",
                new Phone("010-1234-5670"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Reservation reservation1 = reservationRepository.save(new Reservation(
                "아득한 건 언제나 늘 아름답게 보이죠",
                Status.PENDING,
                mentoring,
                mentee1
        ));
        Reservation reservation2 = reservationRepository.save(new Reservation(
                "가까이 다가 선 세상은 내게 뭘 보여 줄까요~",
                Status.COMPLETE,
                mentoring,
                mentee1
        ));
        String adminAccessToken = jwtProvider.createAccessToken(admin.getId());

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("admin/get-admin-mentorings-id-reservation-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", adminAccessToken)
                .when()
                .get("/admin/mentorings/" + mentoring.getId() + "/reservations")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("관리자가 아닌 회원은 관리자용 예약 조회 기능을 요청 시 403 Forbidden이 반환된다")
    @Test
    void findMentoringReservationsFail() {
        // given
        Member normalMember = memberRepository.save(new Member(
                "adminId",
                "MALE",
                "관리자",
                new Phone("010-1111-2222"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Member mentor = memberRepository.save(new Member(
                "mentorId",
                "MALE",
                "아이유",
                new Phone("010-1234-5678"),
                Password.from("password"),
                MemberRole.MENTOR
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "Last Fantasy",
                "아직 모르는 게 많은 나 저 문을 열고 걸어 나가도 되겠죠 날 천천히 기다릴 수 있나요 기도해줘요 넘어지지 않도록",
                "가상의카카오오픈채팅"
        ));
        String normalAccessToken = jwtProvider.createAccessToken(normalMember.getId());

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("admin/get-admin-mentorings-id-reservation-unauthorized"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", normalAccessToken)
                .when()
                .get("/admin/mentorings/" + mentoring.getId() + "/reservations")
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("관리자는 예약의 상태를 변경할 수 있다")
    @Test
    void updateStatusWithAdminAuthorization() {
        // given
        Member admin = memberRepository.save(new Member(
                "adminId",
                "MALE",
                "관리자",
                new Phone("010-1111-2222"),
                Password.from("password"),
                MemberRole.ADMIN
        ));
        Member mentor = memberRepository.save(new Member(
                "mentorId",
                "MALE",
                "고윤하",
                new Phone("010-1234-5678"),
                Password.from("password"),
                MemberRole.MENTOR
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "살별",
                "이 비행의 끝에는 분명 너의 소원이 될 거라고 작은 목소리로 우리의 추억을 빌어볼게",
                "가상의카카오오픈채팅"
        ));
        Member mentee = memberRepository.save(new Member(
                "menteeId1",
                "MALE",
                "김멘티",
                new Phone("010-1234-5679"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Reservation reservation = reservationRepository.save(new Reservation(
                "아득한 건 언제나 늘 아름답게 보이죠",
                Status.PENDING,
                mentoring,
                mentee
        ));
        String adminAccessToken = jwtProvider.createAccessToken(admin.getId());

        ReservationStatusUpdateRequest reservationStatusUpdateRequest
                = new ReservationStatusUpdateRequest(Status.COMPLETE.name());

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("admin/patch-admin-reservations-id-status-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", adminAccessToken)
                .body(reservationStatusUpdateRequest)
                .when()
                .patch("/admin/reservations/" + reservation.getId() + "/status")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("관리자가 등록되어 있는 예약을 삭제하면 204 No Content를 반환한다")
    @Test
    void deleteReservationWithAdminAuthorization() {
        // given
        Member admin = memberRepository.save(new Member(
                "adminId",
                "MALE",
                "관리자",
                new Phone("010-1111-2222"),
                Password.from("password"),
                MemberRole.ADMIN
        ));
        Member mentor = memberRepository.save(new Member(
                "mentorId",
                "MALE",
                "최유리",
                new Phone("010-1234-5678"),
                Password.from("password"),
                MemberRole.MENTOR
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                5000,
                5,
                "잘 지내자, 우리",
                "분명 언젠가 다시 스칠 날 있겠지만 모른척 지나가겠지~",
                "가상의카카오오픈채팅"
        ));
        Member mentee = memberRepository.save(new Member(
                "menteeId1",
                "MALE",
                "김멘티",
                new Phone("010-1234-5679"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Reservation reservation = reservationRepository.save(new Reservation(
                "최선을 다한 넌 받아들이겠지만",
                Status.COMPLETE,
                mentoring,
                mentee
        ));
        Review review = reviewRepository.save(new Review(
                4,
                "서툴렀던 난 아직도 기적을 꿈꾼다",
                reservation,
                mentee
        ));
        String adminAccessToken = jwtProvider.createAccessToken(admin.getId());

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("admin/delete-admin-reservations-id-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", adminAccessToken)
                .when()
                .delete("/admin/reservations/" + mentoring.getId())
                .then().log().all()
                .statusCode(204);
    }
}
