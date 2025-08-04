package fittoring.integration.mentoring.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;

import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.repository.CategoryMentoringRepository;
import fittoring.mentoring.business.repository.CategoryRepository;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
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
                        ArgumentMatchers.anyString(),
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
                        ArgumentMatchers.anyString(),
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
                        ArgumentMatchers.anyString(),
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
}
