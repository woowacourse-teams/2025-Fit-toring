package fittoring.integration.mentoring.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;

import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.CategoryMentoringRepository;
import fittoring.mentoring.business.repository.CategoryRepository;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.service.JwtProvider;
import fittoring.mentoring.infra.SmsRestClientService;
import fittoring.mentoring.presentation.dto.ReservationCreateRequest;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
import fittoring.util.DbCleaner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ReservationControllerTest {

    @LocalServerPort
    public int port;

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

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        dbCleaner.clean();
    }

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

        Category savedCategory = categoryRepository.save(new Category("체형교정"));

        Mentoring savedMentoring = mentoringRepository.save(
                new Mentoring(
                        "멘토링1",
                        "010-1234-5678",
                        1000,
                        3,
                        "멘토링 내용",
                        "멘토링 자기소개"
                )
        );

        categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));

        imageRepository.save(new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentoring.getId()));

        Long mentoringId = savedMentoring.getId();

        ReservationCreateRequest request = new ReservationCreateRequest(
                "멘티",
                "010-1111-2222",
                "멘토링 예약 내용"
        );

        //when
        ReservationCreateResponse response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/mentorings/" + mentoringId + "/reservation")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(ReservationCreateResponse.class);

        //then
        ReservationCreateResponse expected = new ReservationCreateResponse(
                savedMentoring.getMentorName(),
                request.menteeName(),
                savedMentoring.getMentorPhone(),
                request.menteePhone()
        );

        assertThat(response).isEqualTo(expected);
    }

    @DisplayName("멘토링 예약 정보가 잘못되었다면 400 Bad Request 상태코드를 반환한다.")
    @Test
    void createReservation2() {
        //given
        doNothing()
                .when(smsRestClientService)
                .sendSms(
                        ArgumentMatchers.any(Phone.class),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );

        Category savedCategory = categoryRepository.save(new Category("체형교정"));

        Mentoring savedMentoring = mentoringRepository.save(
                new Mentoring(
                        "멘토링1",
                        "010-1234-5678",
                        1000,
                        3,
                        "멘토링 내용",
                        "멘토링 자기소개"
                )
        );

        categoryMentoringRepository.save(new CategoryMentoring(savedCategory, savedMentoring));

        imageRepository.save(new Image("image1.jpg", ImageType.MENTORING_PROFILE, savedMentoring.getId()));

        Long mentoringId = savedMentoring.getId();

        ReservationCreateRequest request = new ReservationCreateRequest(
                "멘티",
                "invalid-phone",
                "멘토링 예약 내용"
        );

        //when
        Response response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/mentorings/" + mentoringId + "/reservation");

        //then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("존재하지 않는 멘토링에 예약을 시도하면 상태코드 404 Not Found를 반환한다.")
    @Test
    void createReservation3() {
        //given
        doNothing()
                .when(smsRestClientService)
                .sendSms(
                        ArgumentMatchers.any(Phone.class),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );

        Long mentoringId = 1L;

        ReservationCreateRequest request = new ReservationCreateRequest(
                "멘티",
                "010-1234-5678",
                "멘토링 예약 내용"
        );

        //when
        Response response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/mentorings/" + mentoringId + "/reservation");

        //then
        assertThat(response.statusCode()).isEqualTo(404);
    }

    @DisplayName("내가 작성한 예약 조회에 성공하면 200 OK를 반환한다")
    @Test
    void findParticipatedReservation() {
        // given
        doNothing()
            .when(smsRestClientService)
            .sendSms(
                ArgumentMatchers.any(Phone.class),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            );

        Member mentor1 = memberRepository.save(new Member(
            "mentorId1",
            "남",
            "김멘토",
            "010-1234-5678",
            Password.from("password")
        ));
        Member mentor2 = memberRepository.save(new Member(
            "mentorId2",
            "남",
            "김멘토",
            "010-1234-5679",
            Password.from("password")
        ));
        Mentoring mentoring1 = mentoringRepository.save(new Mentoring(
            mentor1,
            5_000,
            5,
            5,
            "한 줄 소개",
            "긴 글 소개"
        ));
        Mentoring mentoring2 = mentoringRepository.save(new Mentoring(
            mentor2,
            5_000,
            5,
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
            "남",
            "김멘티",
            "010-5678-1234",
            Password.from("password")
        ));
        Reservation reservation1 = reservationRepository.save(new Reservation(
            mentee,
            "신청 내용1",
            mentoring1
        ));
        Reservation reservation2 = reservationRepository.save(new Reservation(
            mentee,
            "신청 내용2",
            mentoring2
        ));

        // when
        // then
        RestAssured
            .given()
            .log().all().contentType(ContentType.JSON)
            .cookie("accessToken", jwtProvider.createAccessToken(mentee.getId()))
            .when()
            .get("/reservations/participated")
            .then()
            .statusCode(200)
            .body("", hasSize(2));
    }

    @DisplayName("존재하지 않는 멤버가 자신의 예약을 조회하려고 하면 404 Not Found를 반환한다")
    @Test
    void findMemberReservationsFail() {
        // given
        doNothing()
            .when(smsRestClientService)
            .sendSms(
                ArgumentMatchers.any(Phone.class),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            );

        Member mentor = memberRepository.save(new Member(
            "mentorId",
            "남",
            "김멘토",
            "010-1234-5678",
            Password.from("password")
        ));
        Mentoring mentoring = mentoringRepository.save(new Mentoring(
            mentor,
            5_000,
            5,
            5,
            "한 줄 소개",
            "긴 글 소개"
        ));
        Category category = categoryRepository.save(new Category("근육 증진"));
        categoryMentoringRepository.save(new CategoryMentoring(
            category, mentoring
        ));
        Member mentee = memberRepository.save(new Member(
            "menteeId",
            "남",
            "김멘티",
            "010-5678-1234",
            Password.from("password")
        ));
        Reservation reservation = reservationRepository.save(new Reservation(
            mentee,
            "신청 내용",
            mentoring
        ));

        // when
        // then
        RestAssured
            .given()
            .log().all().contentType(ContentType.JSON)
            .cookie("accessToken", jwtProvider.createAccessToken(999L))
            .when()
            .get("/reservations/participated")
            .then()
            .statusCode(404);
    }
}
