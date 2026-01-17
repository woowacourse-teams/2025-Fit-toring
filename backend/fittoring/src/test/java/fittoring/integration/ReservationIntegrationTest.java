package fittoring.integration;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.chat.repository.ChatRoomRepository;
import fittoring.application.image.repository.ImageRepository;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.CategoryMentoringRepository;
import fittoring.application.mentoring.repository.CategoryRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.mentoring.service.dto.MentorMentoringReservationResponse;
import fittoring.application.reservation.presentation.dto.request.ReservationCreateRequest;
import fittoring.application.reservation.presentation.dto.response.ParticipatedReservationResponse;
import fittoring.application.reservation.presentation.dto.response.PhoneNumberResponse;
import fittoring.application.reservation.presentation.dto.response.ReservationCreateResponse;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.domain.model.Category;
import fittoring.domain.model.CategoryMentoring;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Status;
import fittoring.domain.model.password.Password;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;

class ReservationIntegrationTest extends AbstractApiDocumentationTest {

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

    @Autowired
    private ChatRoomRepository chatRoomRepository;

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
                new Member("id1", Gender.MALE, "박멘토", new Phone("010-1234-5678"), Password.from("pw")));
        Member mentee = memberRepository.save(
                new Member("id2", Gender.MALE, "김멘티", new Phone("010-1234-5679"), Password.from("pw")));

        Mentoring savedMentoring = mentoringRepository.save(
                new Mentoring(
                        mentor,
                        1000,
                        3,
                        "멘토링 내용",
                        "멘토링 자기소개"
                )
        );

        Category savedCategory = categoryRepository.save(new Category("체형교정"));
        categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));

        imageRepository.save(new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentoring.getId(), null));

        Long mentoringId = savedMentoring.getId();

        ReservationCreateRequest request = new ReservationCreateRequest("멘토링 예약 내용");

        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

        //when
        ReservationCreateResponse response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/post-mentorings-id-reservation-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("멘토링 예약")
                                .description("멘토링을 예약합니다. 성공 시 201 Created, 실패 시 400 Bad Request 또는 404 Not Found를 반환합니다.")
                                .requestSchema(Schema.schema("ReservationCreateRequest"))
                                .requestFields(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("예약 내용")
                                )
                                .responseSchema(Schema.schema("ReservationCreateResponse"))
                                .responseFields(
                                        fieldWithPath("mentorName").type(JsonFieldType.STRING).description("멘토 이름"),
                                        fieldWithPath("menteeName").type(JsonFieldType.STRING).description("멘티 이름"),
                                        fieldWithPath("menteePhoneNumber").type(JsonFieldType.STRING)
                                                .description("멘티 전화번호")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/mentorings/{mentoringId}/reservation", mentoringId)
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
                Gender.MALE,
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
                "또 봐요 미스터 채플린~~"
        ));

        String mentorAccessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());
        ReservationCreateRequest requestBody = new ReservationCreateRequest(
                "그 이름도 내겐 사랑스런 채플린~"
        );

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/post-mentorings-id-reservation-mentoring-is-mine",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .requestSchema(Schema.schema("ReservationCreateRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", mentorAccessToken)
                .body(requestBody)
                .when()
                .post("/mentorings/{mentoringId}/reservation", mentoring.getId())
                .then()
                .statusCode(400);
    }

    @DisplayName("존재하지 않는 멘토링에 예약을 시도하면 상태코드 404 Not Found를 반환한다.")
    @Test
    void createReservationFail2() {
        //given
        Member mentee = memberRepository.save(
                new Member("id1", Gender.MALE, "김멘티", new Phone("010-1234-5679"), Password.from("pw")));
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());
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
                .filter(documentWithTag("reservation/post-mentorings-id-reservation-not-found",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .requestSchema(Schema.schema("ReservationCreateRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/mentorings/{mentoringId}/reservation", invalidMentoringId);

        //then
        assertThat(response.statusCode()).isEqualTo(404);
    }

    @DisplayName("내가 작성한 예약 조회에 성공하면 200 OK를 반환한다")
    @Test
    void findParticipatedReservation() {
        // given
        Member mentor1 = memberRepository.save(new Member(
                "mentorId1",
                Gender.MALE,
                "김멘토",
                new Phone("010-1234-5678"),
                Password.from("password")
        ));
        Member mentor2 = memberRepository.save(new Member(
                "mentorId2",
                Gender.MALE,
                "김멘토",
                new Phone("010-1234-5679"),
                Password.from("password")
        ));
        Mentoring mentoring1 = mentoringRepository.save(new Mentoring(
                mentor1,
                5_000,
                5,
                "한 줄 소개",
                "긴 글 소개"
        ));
        Mentoring mentoring2 = mentoringRepository.save(new Mentoring(
                mentor2,
                5_000,
                5,
                "한 줄 소개",
                "긴 글 소개"
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
                Gender.MALE,
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

        /*
                Long reservationId,
        Long mentoringId,
        String mentorName,
        String mentorProfileImage,
        LocalDate reservedAt,
        String content,
        String status,
        Long chatRoomId,
        boolean isReviewed

         */

        // when
        List<ParticipatedReservationResponse> response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/get-reservations-participated-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("내 예약 조회")
                                .description("내가 신청한 예약 목록을 조회합니다. 성공 시 200 OK를 반환합니다.")
                                .responseSchema(Schema.schema("ParticipatedReservationResponse"))
                                .responseFields(
                                        fieldWithPath("[].reservationId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("예약 ID"),
                                        fieldWithPath("[].mentoringId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("멘토링 ID"),
                                        fieldWithPath("[].mentorName")
                                                .type(JsonFieldType.STRING)
                                                .description("멘토 이름"),
                                        fieldWithPath("[].mentorProfileImage")
                                                .type(JsonFieldType.STRING)
                                                .description("멘토 프로필 이미지 URL")
                                                .optional(),
                                        fieldWithPath("[].reservedAt")
                                                .type(JsonFieldType.STRING)
                                                .description("예약 날짜"),
                                        fieldWithPath("[].content")
                                                .type(JsonFieldType.STRING)
                                                .description("예약 내용"),
                                        fieldWithPath("[].status")
                                                .type(JsonFieldType.STRING)
                                                .description("예약 상태"),
                                        fieldWithPath("[].chatRoomId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("채팅방 ID")
                                                .optional(),
                                        fieldWithPath("[].isReviewed")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("리뷰 작성 여부")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", jwtProvider.createAccessToken(mentee.getId(), mentee.getRole()))
                .when()
                .get("/reservations/participated")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(response).hasSize(2);
    }

    @DisplayName("멘토가 개설한 단일 멘토링의 모든 예약을 조회하면 상태코드 200 OK와 예약 정보를 반환한다.")
    @Test
    void getReservationsByMentor() {
        //given
        //멘티 생성
        Member mentor = memberRepository.save(
                new Member("id1",
                        Gender.MALE,
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId(), savedMentor.getRole());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(
                mentor,
                1000,
                3,
                "멘토링 내용",
                "멘토링 자기소개"
        );
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        Gender.MALE,
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
                .filter(documentWithTag("reservation/get-mentorings-mine-reservation-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("멘토링 예약 조회 (멘토)")
                                .description("멘토가 개설한 멘토링의 예약 목록을 조회합니다. 성공 시 200 OK를 반환합니다.")
                                .responseSchema(Schema.schema("MentorMentoringReservationResponse"))
                                .responseFields(
                                        fieldWithPath("[].reservationId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("예약 ID"),

                                        fieldWithPath("[].menteeName")
                                                .type(JsonFieldType.STRING)
                                                .description("멘티 이름"),

                                        fieldWithPath("[].phoneNumber")
                                                .type(JsonFieldType.STRING)
                                                .description("멘티 전화번호")
                                                .optional(),

                                        fieldWithPath("[].price")
                                                .type(JsonFieldType.NUMBER)
                                                .description("멘토링 가격"),

                                        fieldWithPath("[].content")
                                                .type(JsonFieldType.STRING)
                                                .description("멘토링 요청 내용"),

                                        fieldWithPath("[].status")
                                                .type(JsonFieldType.STRING)
                                                .description("예약 상태 (PENDING, APPROVED, REJECTED)"),

                                        fieldWithPath("[].chatRoomId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("채팅방 ID")
                                                .optional(),

                                        fieldWithPath("[].chatStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("채팅방 상태")
                                                .optional(),

                                        fieldWithPath("[].createdAt")
                                                .type(JsonFieldType.STRING)
                                                .description("예약 생성 일시")
                                )
                                .build())))
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
        MentorMentoringReservationResponse expected = MentorMentoringReservationResponse.of(savedReservation, null);
        MentorMentoringReservationResponse expected2 = MentorMentoringReservationResponse.of(savedReservation2, null);
        MentorMentoringReservationResponse expected3 = MentorMentoringReservationResponse.of(savedReservation3, null);
        MentorMentoringReservationResponse expected4 = MentorMentoringReservationResponse.of(savedReservation4, null);

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
                        Gender.MALE,
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId(), savedMentor.getRole());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        Mentoring mentoring2 = new Mentoring(mentor, 1500, 3, "멘토링 내용2", "멘토링 자기소개2");
        Mentoring savedMentoring2 = mentoringRepository.save(mentoring2);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        Gender.MALE,
                        "김멘티",
                        new Phone("010-5678-9123"),
                        Password.from("pw"))
        );
        Member savedMentee = memberRepository.save(mentee);

        Member mentee2 = memberRepository.save(
                new Member("id3",
                        Gender.MALE,
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
        ChatRoom chatRoom4 = chatRoomRepository.save(
                new ChatRoom(savedReservation4.getId(), savedMentee.getId(), mentor.getId())
        );

        //mentee2의 예약
        Reservation savedReservation5 = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.PENDING, savedMentoring2, savedMentee2)
        );
        Reservation savedReservation6 = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.COMPLETE, savedMentoring2, savedMentee2)
        );
        ChatRoom chatRoom6 = chatRoomRepository.save(
                new ChatRoom(savedReservation6.getId(), savedMentee2.getId(), mentor.getId())
        );

        //when
        List<MentorMentoringReservationResponse> response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/get-mentorings-mine-reservation-success-multiple",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("멘토링 예약 조회 (멘토 - 다중)")
                                .description("멘토가 개설한 여러 멘토링의 예약 목록을 조회합니다.")
                                .responseSchema(Schema.schema("MentorMentoringReservationResponse"))
                                .build())))
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
        MentorMentoringReservationResponse expected = MentorMentoringReservationResponse.of(savedReservation, null);
        MentorMentoringReservationResponse expected2 = MentorMentoringReservationResponse.of(savedReservation2, null);
        MentorMentoringReservationResponse expected3 = MentorMentoringReservationResponse.of(savedReservation3, null);
        MentorMentoringReservationResponse expected4 = MentorMentoringReservationResponse.of(savedReservation4,
                chatRoom4);
        MentorMentoringReservationResponse expected5 = MentorMentoringReservationResponse.of(savedReservation5, null);
        MentorMentoringReservationResponse expected6 = MentorMentoringReservationResponse.of(savedReservation6,
                chatRoom6);

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
                        Gender.MALE,
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId(), savedMentor.getRole());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //when
        List<MentorMentoringReservationResponse> response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/get-mentorings-mine-reservation-empty-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("멘토링 예약 조회 (멘토 - 빈 목록)")
                                .description("예약이 없는 경우 빈 목록을 반환합니다.")
                                .responseSchema(Schema.schema("MentorMentoringReservationResponse"))
                                .build())))
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

    @DisplayName("예약이 승인되면 sms를 전송하고, 200 OK를 반환한다.")
    @Test
    void approveStatus() {
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
                        Gender.MALE,
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId(), savedMentor.getRole());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        Gender.MALE,
                        "김멘티",
                        new Phone("010-5678-9123"),
                        Password.from("pw"))
        );
        Member savedMentee = memberRepository.save(mentee);
        Reservation savedReservation = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.PENDING, savedMentoring, savedMentee)
        );

        //when
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/patch-reservations-id-approve-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("예약 승인")
                                .description("예약을 승인합니다. 성공 시 200 OK, 실패 시 400 Bad Request를 반환합니다.")
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .patch("/reservations/{reservationId}/approve", savedReservation.getId())
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

    @DisplayName("예약이 거절되면 sms를 전송하고, 200 OK를 반환한다.")
    @Test
    void rejectStatus() {
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
                        Gender.MALE,
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId(), savedMentor.getRole());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        Gender.MALE,
                        "김멘티",
                        new Phone("010-5678-9123"),
                        Password.from("pw"))
        );
        Member savedMentee = memberRepository.save(mentee);
        Reservation savedReservation = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.PENDING, savedMentoring, savedMentee)
        );

        //when
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/patch-reservations-id-reject-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("예약 거절")
                                .description("예약을 거절합니다. 성공 시 200 OK, 실패 시 400 Bad Request를 반환합니다.")
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .patch("/reservations/{reservationId}/reject", savedReservation.getId())
                .then().log().all()
                .statusCode(200);

        //then
        assertThat(reservationRepository.findById(savedReservation.getId()))
                .isPresent()
                .hasValueSatisfying(
                        reservation ->
                                assertThat(reservation.getStatus()).isEqualTo(Status.REJECTED.name())
                );
    }

    @DisplayName("이미 처리(완료, 승인, 거절)된 예약을 승인하면 400 Bad Request가 발생한다.")
    @Test
    void updateStatus2() {
        //given
        Member mentor = memberRepository.save(
                new Member("id1",
                        Gender.MALE,
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId(), savedMentor.getRole());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        Gender.MALE,
                        "김멘티",
                        new Phone("010-5678-9123"),
                        Password.from("pw"))
        );
        Member savedMentee = memberRepository.save(mentee);

        Reservation savedReservation = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.COMPLETE, savedMentoring, savedMentee)
        );

        //when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/patch-reservations-id-status-already-patched",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("예약 승인 실패 - 이미 처리됨")
                                .description("이미 처리된 예약을 승인할 수 없습니다.")
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .patch("/reservations/{reservationId}/approve", savedReservation.getId());

        //then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("이미 처리된(거절, 승인, 완료) 예약을 거절하면 400 Bad Request가 발생한다.")
    @Test
    void updateStatus3() {
        //given
        Member mentor = memberRepository.save(
                new Member("id1",
                        Gender.MALE,
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId(), savedMentor.getRole());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        Gender.MALE,
                        "김멘티",
                        new Phone("010-5678-9123"),
                        Password.from("pw"))
        );
        Member savedMentee = memberRepository.save(mentee);

        Reservation savedReservation = reservationRepository.save(
                new Reservation("멘토링 예약 내용", Status.APPROVED, savedMentoring, savedMentee)
        );

        //when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/patch-reservations-id-status-already-patched-reject",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("예약 거절 실패 - 이미 처리됨")
                                .description("이미 처리된 예약을 거절할 수 없습니다.")
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .patch("/reservations/{reservationId}/reject", savedReservation.getId());

        //then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("예약이 완료되면 200 OK를 반환한다.")
    @Test
    void completeStatus() {
        //given
        Member savedMentor = memberRepository.save(FixtureUtil.getTestMentor());

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId(), savedMentor.getRole());

        //멘토링 생성
        Mentoring savedMentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(savedMentor));

        //멘티 생성
        Member savedMentee = memberRepository.save(FixtureUtil.getTestMentee());
        Reservation savedReservation = reservationRepository.save(
                FixtureUtil.getTestApprovedReservation(savedMentoring, savedMentee)
        );

        //when
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/patch-reservations-id-complete-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("예약 완료")
                                .description("예약을 완료 처리합니다. 성공 시 200 OK, 실패 시 400 Bad Request 또는 403 Forbidden을 반환합니다.")
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .patch("/reservations/{reservationId}/complete", savedReservation.getId())
                .then().log().all()
                .statusCode(200);

        //then
        assertThat(reservationRepository.findById(savedReservation.getId()))
                .isPresent()
                .hasValueSatisfying(
                        reservation ->
                                assertThat(reservation.getStatus()).isEqualTo(Status.COMPLETE.name())
                );
    }

    @DisplayName("승인되지 않은 예약을 완료하면 400 Bad Request가 발생한다.")
    @Test
    void completeStatusFail() {
        //given
        Member savedMentor = memberRepository.save(FixtureUtil.getTestMentor());

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId(), savedMentor.getRole());

        //멘토링 생성
        Mentoring savedMentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(savedMentor));

        //멘티 생성
        Member savedMentee = memberRepository.save(FixtureUtil.getTestMentee());
        Reservation savedReservation = reservationRepository.save(
                FixtureUtil.getTestPendingReservation(savedMentoring, savedMentee)
        );

        //when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/patch-reservations-id-complete-fail",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("예약 완료 실패 - 미승인")
                                .description("승인되지 않은 예약을 완료 처리할 수 없습니다.")
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .patch("/reservations/{reservationId}/complete", savedReservation.getId());

        //then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("자신의 예약이 아닌 예약을 완료하면 403 Forbidden이 발생한다.")
    @Test
    void completeStatusFail2() {
        //given
        Member savedMentor = memberRepository.save(FixtureUtil.getTestMentor());
        Member savedOtherMentor = memberRepository.save(FixtureUtil.getTestMentor(1));

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedOtherMentor.getId(), savedOtherMentor.getRole());

        //멘토링 생성
        Mentoring savedMentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(savedMentor));

        //멘티 생성
        Member savedMentee = memberRepository.save(FixtureUtil.getTestMentee());
        Reservation savedReservation = reservationRepository.save(
                FixtureUtil.getTestApprovedReservation(savedMentoring, savedMentee)
        );

        //when //then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/patch-reservations-forbidden-fail",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("예약 완료 실패 - 권한 없음")
                                .description("자신의 예약이 아닌 경우 완료 처리할 수 없습니다.")
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .patch("/reservations/{reservationId}/complete", savedReservation.getId())
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("예약자의 전화번호 요청하면 200 OK와 전화번호를 반환한다.")
    @Test
    void getPhone() {
        //given
        Member mentor = memberRepository.save(
                new Member("id1",
                        Gender.MALE,
                        "박멘토",
                        new Phone("010-1234-5679"),
                        Password.from("pw"))
        );
        Member savedMentor = memberRepository.save(mentor);

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(savedMentor.getId(), savedMentor.getRole());

        //멘토링 생성
        Mentoring mentoring = new Mentoring(mentor, 1000, 3, "멘토링 내용", "멘토링 자기소개");
        Mentoring savedMentoring = mentoringRepository.save(mentoring);

        //멘티 생성
        Member mentee = memberRepository.save(
                new Member("id2",
                        Gender.MALE,
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
                .filter(documentWithTag("reservation/get-reservations-id-phoneNumber-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("예약")
                                .summary("예약자 전화번호 조회")
                                .description("예약자의 전화번호를 조회합니다. 성공 시 200 OK를 반환합니다.")
                                .responseSchema(Schema.schema("PhoneNumberResponse"))
                                .responseFields(
                                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .get("/reservations/{reservationId}/phone", savedReservation.getId())
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(PhoneNumberResponse.class);

        //then
        assertThat(response.phoneNumber()).isEqualTo(savedMentee.getPhoneNumber());
    }
}
