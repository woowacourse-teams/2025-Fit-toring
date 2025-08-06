package fittoring.integration.mentoring.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;

import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.CategoryMentoringRepository;
import fittoring.mentoring.business.repository.CategoryRepository;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
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
    private CategoryMentoringRepository categoryMentoringRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProvider jwtProvider;


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
                        "멘토링 자기소개"
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
                .given()
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
                mentor.getPhoneNumber(),
                mentee.getPhoneNumber(),
                request.content()
        );

        assertThat(response).isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 멘토링에 예약을 시도하면 상태코드 404 Not Found를 반환한다.")
    @Test
    void createReservation2() {
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
                .given()
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/mentorings/" + invalidMentoringId + "/reservation");

        //then
        assertThat(response.statusCode()).isEqualTo(404);
    }
}
