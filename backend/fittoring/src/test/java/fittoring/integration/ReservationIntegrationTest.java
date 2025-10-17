package fittoring.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;

import fittoring.application.auth.service.JwtProvider;
import fittoring.application.image.repository.ImageRepository;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.CategoryMentoringRepository;
import fittoring.application.mentoring.repository.CategoryRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.mentoring.service.dto.MentorMentoringReservationResponse;
import fittoring.application.reservation.presentation.dto.request.ReservationCreateRequest;
import fittoring.application.reservation.presentation.dto.request.ReservationStatusUpdateRequest;
import fittoring.application.reservation.presentation.dto.response.PhoneNumberResponse;
import fittoring.application.reservation.presentation.dto.response.ReservationCreateResponse;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.domain.model.Category;
import fittoring.domain.model.CategoryMentoring;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Status;
import fittoring.domain.model.password.Password;
import fittoring.infrastructure.SmsRestClientService;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class ReservationIntegrationTest extends AbstractApiDocumentationTest {

    @MockitoBean
    private SmsRestClientService smsRestClientService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CategoryMentoringRepository categoryMentoringRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("멘토링 예약에 성공하면 201 Created 상태코드와 예약 정보를 반환한다.")
    @Test
    void createReservation() {
        //given
        doNothing()
                .when(smsRestClientService)
                .sendSms(
                        ArgumentMatchers.any(Phone.class),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );

        Member mentor = memberRepository.save(
                new Member("id1", "MALE", "박멘토", new Phone("010-1234-5678"), Password.from("pw")));
        Member mentee = memberRepository.save(
                new Member("id2", "MALE", "김멘티", new Phone("010-1234-5679"), Password.from("pw")));

        Mentoring savedMentoring = mentoringRepository.save(
                new Mentoring(
                        mentor,
                        1000,
                        3,
                        "멘토링 내용",
                        "멘토링 자기소개",
                        "가상의카카오오픈채팅"
                )
        );

        Category savedCategory = categoryRepository.save(new Category("체형교정"));
        categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));

        imageRepository.save(new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentoring.getId()));

        Long mentoringId = savedMentoring.getId();

        ReservationCreateRequest request = new ReservationCreateRequest("멘토링 예약 내용");

        String accessToken = jwtProvider.createAccessToken(mentee.getId());

        //when
        ReservationCreateResponse response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/post-mentorings-id-reservation-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/mentorings/" + mentoringId + "/reservation")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(ReservationCreateResponse.class);

        //then
        ReservationCreateResponse expected = new ReservationCreateResponse(
                mentor.getName(),
                mentee.getName(),
                mentee.getPhoneNumber()
        );

        assertThat(response).isEqualTo(expected);
    }

    @DisplayName("본인이 개설한 멘토링에 예약하려고 하면 400 Bad Request를 반환한다")
    @Test
    void createReservationFail1() {
        // given
        Member mentor = memberRepository.save(new Member(
                "mentorLoginId",
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
                "모던 타임즈",
                "또 봐요 미스터 채플린~~",
                "가상의카카오오픈채팅"
        ));

        String mentorAccessToken = jwtProvider.createAccessToken(mentor.getId());
        ReservationCreateRequest requestBody = new ReservationCreateRequest(
                "그 이름도 내겐 사랑스런 채플린~"
        );

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/post-mentorings-id-reservation-mentoring-is-mine"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", mentorAccessToken)
                .body(requestBody)
                .when()
                .post("/mentorings/" + mentoring.getId() + "/reservation")
                .then()
                .statusCode(400);
    }

    @DisplayName("존재하지 않는 멘토링에 예약을 시도하면 상태코드 404 Not Found를 반환한다.")
    @Test
    void createReservationFail2() {
        //given
        Member mentee = memberRepository.save(
                new Member("id1", "MALE", "김멘티", new Phone("010-1234-5679"), Password.from("pw")));
        String accessToken = jwtProvider.createAccessToken(mentee.getId());
        doNothing()
                .when(smsRestClientService)
                .sendSms(
                        ArgumentMatchers.any(Phone.class),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );

        Long invalidMentoringId = 1L;

        ReservationCreateRequest request = new ReservationCreateRequest(
                "멘토링 예약 내용"
        );

        //when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/post-mentorings-id-reservation-not-found"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/mentorings/" + invalidMentoringId + "/reservation");

        //then
        assertThat(response.statusCode()).isEqualTo(404);
    }

    @DisplayName("내가 작성한 예약 조회에 성공하면 200 OK를 반환한다")
    @Test
    void findParticipatedReservation() {
        // given
        Member mentor1 = memberRepository.save(new Member(
                "mentorId1",
                "남",
                "김멘토",
                new Phone("010-1234-5678"),
                Password.from("password")
        ));
        Member mentor2 = memberRepository.save(new Member(
                "mentorId2",
                "남",
                "김멘토",
                new Phone("010-1234-5679"),
                Password.from("password")
        ));
        Mentoring mentoring1 = mentoringRepository.save(new Mentoring(
                mentor1,
                5_000,
                5,
                "한 줄 소개",
                "긴 글 소개",
                "가상의카카오오픈채팅"
        ));
        Mentoring mentoring2 = mentoringRepository.save(new Mentoring(
                mentor2,
                5_000,
                5,
                "한 줄 소개",
                "긴 글 소개",
                "가상의카카오오픈채팅"
        ));
        Category category1 = categoryRepository.save(new Category("근육 증진"));
        Category category2 = categoryRepository.save(new Category("다이어트"));
        Category category3 = categoryRepository.save(new Category("보디 빌딩"));
        categoryMentoringRepository.save(new CategoryMentoring(
                category1, mentoring1
        ));
        categoryMentoringRepository.save(new CategoryMentoring(
                category2, mentoring1
        ));
        categoryMentoringRepository.save(new CategoryMentoring(
                category3, mentoring2
        ));
        Member mentee = memberRepository.save(new Member(
                "menteeId",
                "남",
                "김멘티",
                new Phone("010-5678-1234"),
                Password.from("password")
        ));
        reservationRepository.save(new Reservation(
                "신청 내용1",
                Status.PENDING,
                mentoring1,
                mentee
        ));
        reservationRepository.save(new Reservation(
                "신청 내용2",
                Status.PENDING,
                mentoring2,
                mentee

        ));

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/get-reservations-participated-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", jwtProvider.createAccessToken(mentee.getId()))
                .when()
                .get("/reservations/participated")
                .then()
                .statusCode(200)
                .body("", hasSize(2));
    }

    @DisplayName("멘토가 개설한 단일 멘토링의 모든 예약을 조회하면 상태코드 200 OK와 예약 정보를 반환한다.")
    @Test
    void getReservationsByMentor() {
        //given
        //멘티 생성
        Member mentor = memberRepository.save(
                new Member("id1",
                        "MALE",
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개",
                "가상의카카오오픈채팅");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        "MALE",
                        "김멘티",
                        new Phone("010-5678-9123"),
                        Password.from("pw"))
        );
        Member savedMentee = memberRepository.save(mentee);

        //예약 생성
        Reservation savedReservation = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.PENDING, savedMentoring, savedMentee)
        );
        Reservation savedReservation2 = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.PENDING, savedMentoring, savedMentee)
        );
        Reservation savedReservation3 = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.PENDING, savedMentoring, savedMentee)
        );
        Reservation savedReservation4 = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.PENDING, savedMentoring, savedMentee)
        );

        //when
        List<MentorMentoringReservationResponse> response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/get-mentorings-mine-reservation-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .get("/mentorings/mine/reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        //then
        MentorMentoringReservationResponse expected = MentorMentoringReservationResponse.of(savedReservation);
        MentorMentoringReservationResponse expected2 = MentorMentoringReservationResponse.of(savedReservation2);
        MentorMentoringReservationResponse expected3 = MentorMentoringReservationResponse.of(savedReservation3);
        MentorMentoringReservationResponse expected4 = MentorMentoringReservationResponse.of(savedReservation4);

        assertThat(response)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt")
                .containsExactlyInAnyOrder(expected, expected2, expected3, expected4);
    }

    @DisplayName("멘토가 개설한 복수개의 멘토링의 모든 예약을 조회하면 상태코드 200 OK와 예약 정보를 반환한다.")
    @Test
    void getReservationsByMentor2() {
        //given
        //멘티 생성
        Member mentor = memberRepository.save(
                new Member("id1",
                        "MALE",
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개",
                "가상의카카오오픈채팅");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        Mentoring mentoring2 = new Mentoring(mentor, 1500, 3, "멘토링 내용2", "멘토링 자기소개2",
                "가상의카카오오픈채팅");
        Mentoring savedMentoring2 = mentoringRepository.save(mentoring2);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        "MALE",
                        "김멘티",
                        new Phone("010-5678-9123"),
                        Password.from("pw"))
        );
        Member savedMentee = memberRepository.save(mentee);

        Member mentee2 = memberRepository.save(
                new Member("id3",
                        "MALE",
                        "이멘티",
                        new Phone("010-1357-2468"),
                        Password.from("pw"))
        );
        Member savedMentee2 = memberRepository.save(mentee2);

        //예약 생성
        //mentee1의 예약
        Reservation savedReservation = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.PENDING, savedMentoring, savedMentee)
        );
        Reservation savedReservation2 = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.PENDING, savedMentoring, savedMentee)
        );
        Reservation savedReservation3 = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.REJECTED, savedMentoring, savedMentee)
        );
        Reservation savedReservation4 = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.APPROVED, savedMentoring2, savedMentee)
        );

        //mentee2의 예약
        Reservation savedReservation5 = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.PENDING, savedMentoring2, savedMentee2)
        );
        Reservation savedReservation6 = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.COMPLETE, savedMentoring2, savedMentee2)
        );

        //when
        List<MentorMentoringReservationResponse> response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .get("/mentorings/mine/reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        //then
        MentorMentoringReservationResponse expected = MentorMentoringReservationResponse.of(savedReservation);
        MentorMentoringReservationResponse expected2 = MentorMentoringReservationResponse.of(savedReservation2);
        MentorMentoringReservationResponse expected3 = MentorMentoringReservationResponse.of(savedReservation3);
        MentorMentoringReservationResponse expected4 = MentorMentoringReservationResponse.of(savedReservation4);
        MentorMentoringReservationResponse expected5 = MentorMentoringReservationResponse.of(savedReservation5);
        MentorMentoringReservationResponse expected6 = MentorMentoringReservationResponse.of(savedReservation6);

        assertThat(response)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt")
                .containsExactlyInAnyOrder(expected, expected2, expected3, expected4, expected5, expected6);
    }

    @DisplayName("멘토가 개설한 멘토링의 예약이 존재하지 않으면 상태코드 200 OK와 빈 리스트를 반환한다.")
    @Test
    void getReservationsByMentor3() {
        //given
        //멘토 생성
        Member mentor = memberRepository.save(
                new Member("id1",
                        "MALE",
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개",
                "가상의카카오오픈채팅");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //when
        List<MentorMentoringReservationResponse> response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/get-mentorings-mine-reservation-empty-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .get("/mentorings/mine/reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        //then
        assertThat(response).isEmpty();
    }

    @DisplayName("예약의 상태가 대기(PENDING)에서 승인(APPROVE)으로 변경되면 sms를 전송하고, 200 OK를 반환한다.")
    @Test
    void updateStatus() {
        //given
        doNothing()
                .when(smsRestClientService)
                .sendSms(
                        ArgumentMatchers.any(Phone.class),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );

        Member mentor = memberRepository.save(
                new Member("id1",
                        "MALE",
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개",
                "가상의카카오오픈채팅");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        "MALE",
                        "김멘티",
                        new Phone("010-5678-9123"),
                        Password.from("pw"))
        );
        Member savedMentee = memberRepository.save(mentee);
        Reservation savedReservation = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.PENDING, savedMentoring, savedMentee)
        );

        ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest("APPROVED");

        //when
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/patch-reservations-id-status-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .body(request)
                .patch("/reservations/" + savedReservation.getId() + "/status")
                .then().log().all()
                .statusCode(200);

        //then
        assertThat(reservationRepository.findById(savedReservation.getId()))
                .isPresent()
                .hasValueSatisfying(
                        reservation ->
                                assertThat(reservation.getStatus()).isEqualTo(Status.APPROVED.name())
                );
    }

    @DisplayName("이미 처리(완료, 승인, 거절)된 예약의 상태를 변경하면 400 Bad Request가 발생한다.")
    @Test
    void updateStatus2() {
        //given
        Member mentor = memberRepository.save(
                new Member("id1",
                        "MALE",
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개",
                "가상의카카오오픈채팅");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        "MALE",
                        "김멘티",
                        new Phone("010-5678-9123"),
                        Password.from("pw"))
        );
        Member savedMentee = memberRepository.save(mentee);

        Reservation savedReservation = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.COMPLETE, savedMentoring, savedMentee)
        );

        ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest("APPROVED");

        //when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/patch-reservations-id-status-already-patched"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .body(request)
                .patch("/reservations/" + savedReservation.getId() + "/status");

        //then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("예약의 상태를 현재 상태와 동일한 상태로 변경하면 400 Bad Request가 발생한다.")
    @Test
    void updateStatus3() {
        //given
        Member mentor = memberRepository.save(
                new Member("id1",
                        "MALE",
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개",
                "가상의카카오오픈채팅");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        "MALE",
                        "김멘티",
                        new Phone("010-5678-9123"),
                        Password.from("pw"))
        );
        Member savedMentee = memberRepository.save(mentee);

        Reservation savedReservation = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.APPROVED, savedMentoring, savedMentee)
        );

        ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest("APPROVED");

        //when
        Response response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .body(request)
                .patch("/reservations/" + savedReservation.getId() + "/status");

        //then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("예약자의 전화번호 요청하면 200 OK와 전화번호를 반환한다.")
    @Test
    void getPhone() {
        //given
        Member mentor = memberRepository.save(
                new Member("id1",
                        "MALE",
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개",
                "가상의카카오오픈채팅");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        "MALE",
                        "김멘티",
                        new Phone("010-5678-9123"),
                        Password.from("pw"))
        );
        Member savedMentee = memberRepository.save(mentee);

        Reservation savedReservation = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.APPROVED, savedMentoring, savedMentee)
        );

        //when
        PhoneNumberResponse response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/get-reservations-id-phone-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .get("/reservations/" + savedReservation.getId() + "/phone")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(PhoneNumberResponse.class);

        //then
        assertThat(response.phoneNumber()).isEqualTo(savedMentee.getPhoneNumber());
    }
}

